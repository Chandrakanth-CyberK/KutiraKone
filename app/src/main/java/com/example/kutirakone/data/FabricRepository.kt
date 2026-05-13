package com.example.kutirakone.data

import android.net.Uri
import android.util.Log
import com.example.kutirakone.model.FabricItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FabricRepository {
    private val firestore = FirebaseFirestore.getInstance()
    // Explicitly using the bucket URL from your config
    private val storage = FirebaseStorage.getInstance("gs://kutirakone-32ad0.firebasestorage.app")
    private val auth = FirebaseAuth.getInstance()

    private val fabricsCollection = firestore.collection("fabrics")

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    suspend fun signUp(email: String, pass: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, pass: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun signInAnonymously() {
        try {
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
                Log.d("FabricRepository", "Anonymous sign-in successful: ${auth.currentUser?.uid}")
            }
        } catch (e: Exception) {
            Log.e("FabricRepository", "Anonymous sign-in failed", e)
        }
    }

    fun getFabrics(materialFilter: String? = null): Flow<List<FabricItem>> = callbackFlow {
        var query: Query = fabricsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
        
        if (!materialFilter.isNullOrEmpty() && materialFilter != "All") {
            query = query.whereEqualTo("materialType", materialFilter)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FabricRepository", "Firestore error: ${error.message}", error)
                close(error)
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(FabricItem::class.java) ?: emptyList()
            Log.d("FabricRepository", "Fetched ${items.size} items from Firestore")
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    suspend fun uploadFabric(
        imageUri: Uri,
        materialType: String,
        size: String,
        description: String
    ) {
        if (auth.currentUser == null) {
            Log.d("FabricRepository", "User not signed in, attempting sign-in before upload...")
            signInAnonymously()
        }
        
        val userId = auth.currentUser?.uid ?: throw Exception("Authentication failed. Please check if Anonymous Auth is enabled in Firebase Console.")
        val fileName = UUID.randomUUID().toString()
        val storageRef = storage.reference.child("fabrics/$fileName")
        
        Log.d("FabricRepository", "Starting upload to: ${storageRef.path}")
        storageRef.putFile(imageUri).await()
        Log.d("FabricRepository", "Upload successful, getting download URL...")
        val imageUrl = storageRef.downloadUrl.await().toString()

        val docRef = fabricsCollection.document()
        val fabricItem = FabricItem(
            id = docRef.id,
            imageUrl = imageUrl,
            materialType = materialType,
            size = size,
            description = description,
            userId = userId,
            timestamp = System.currentTimeMillis()
        )
        docRef.set(fabricItem).await()
    }

    suspend fun deleteFabric(item: FabricItem) {
        if (item.userId == getCurrentUserId()) {
            fabricsCollection.document(item.id).delete().await()
            try {
                storage.getReferenceFromUrl(item.imageUrl).delete().await()
            } catch (e: Exception) {
                // Ignore if storage deletion fails
            }
        }
    }
}
