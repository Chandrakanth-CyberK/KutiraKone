package com.example.kutirakone.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kutirakone.data.FabricRepository
import com.example.kutirakone.model.FabricItem
import com.example.kutirakone.model.SwapRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID

class FabricViewModel : ViewModel() {
    private val repository = FabricRepository()

    private val sampleFabrics = listOf(
        FabricItem("s1", "https://images.unsplash.com/photo-1524230572899-a752b3835840", "Silk", "1.2 meters", "Premium red mulberry silk.", "sample"),
        FabricItem("s2", "https://images.unsplash.com/photo-1606760227091-3dd870d97f1d", "Cotton", "0.8 meters", "Organic blue cotton weave.", "sample"),
        FabricItem("s3", "https://images.unsplash.com/photo-1597484661643-2f5fef640dd1", "Wool", "2.5 meters", "High-quality grey merino wool.", "sample"),
        FabricItem("s4", "https://images.unsplash.com/photo-1583338917451-face2751d8d5", "Silk", "0.5 meters", "Soft velvet silk scrap.", "sample"),
        FabricItem("s5", "https://images.unsplash.com/photo-1614676471928-2ed0ad1061a4", "Cotton", "1.5 meters", "Pure white linen-cotton blend.", "sample"),
        FabricItem("s6", "https://images.unsplash.com/photo-1542272604-787c3835535d", "Wool", "1.0 meters", "Rugged denim-style wool fabric.", "sample"),
        FabricItem("s7", "https://images.unsplash.com/photo-1512436991641-6745cdb1723f", "Silk", "2.0 meters", "Smooth gold satin silk.", "sample"),
        FabricItem("s8", "https://images.unsplash.com/photo-1550537687-c91072c4792d", "Cotton", "0.3 meters", "Intricate white lace cotton.", "sample")
    )

    private val _fabrics = MutableStateFlow<List<FabricItem>>(sampleFabrics)
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _materialFilter = MutableStateFlow("All")
    val materialFilter: StateFlow<String> = _materialFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _displayFabrics = MutableStateFlow<List<FabricItem>>(emptyList())
    val displayFabrics: StateFlow<List<FabricItem>> = _displayFabrics.asStateFlow()

    private val _localFabrics = MutableStateFlow<List<FabricItem>>(emptyList())
    
    private val _swapRequests = MutableStateFlow<List<SwapRequest>>(emptyList())
    val swapRequests: StateFlow<List<SwapRequest>> = _swapRequests.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // We'll still sign in for any other Firebase features, 
                // but we won't strictly depend on Firestore for the demo.
                repository.signInAnonymously()
            } catch (e: Exception) {
                // Ignore
            }
        }
        
        viewModelScope.launch {
            combine(_fabrics, _localFabrics, _searchQuery, _materialFilter) { samples, locals, query, filter ->
                val allFabrics = samples + locals
                var result = allFabrics
                if (query.isNotEmpty()) {
                    result = result.filter {
                        it.materialType.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
                    }
                }
                if (filter != "All") {
                    result = result.filter { it.materialType == filter }
                }
                result
            }.collect {
                _displayFabrics.value = it
            }
        }
    }

    // This is now purely local for the session
    fun uploadFabric(
        imageUri: Uri,
        materialType: String,
        size: String,
        description: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val newItem = FabricItem(
                id = UUID.randomUUID().toString(),
                imageUrl = imageUri.toString(), // Use local URI for display
                materialType = materialType,
                size = size,
                description = description,
                userId = repository.getCurrentUserId() ?: "guest",
                timestamp = System.currentTimeMillis()
            )
            
            _localFabrics.value = _localFabrics.value + newItem
            onSuccess()
        } catch (e: Exception) {
            onError(e.message ?: "Local upload failed")
        }
    }

    fun deleteFabric(item: FabricItem) {
        _localFabrics.value = _localFabrics.value.filter { it.id != item.id }
    }

    fun setFilter(filter: String) {
        _materialFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getCurrentUserId() = repository.getCurrentUserId()

    fun isUserLoggedIn() = repository.isUserLoggedIn()

    fun login(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, pass)
            if (result.isSuccess) onSuccess() else onError(result.exceptionOrNull()?.message ?: "Login failed")
        }
    }

    fun signUp(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signUp(email, pass)
            if (result.isSuccess) onSuccess() else onError(result.exceptionOrNull()?.message ?: "Signup failed")
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.signInWithGoogle(idToken)
            if (result.isSuccess) onSuccess() else onError(result.exceptionOrNull()?.message ?: "Google Sign-In failed")
        }
    }

    fun logout(onSuccess: () -> Unit) {
        repository.logout()
        onSuccess()
    }

    fun sendSwapRequest(fabricItem: FabricItem) {
        val request = SwapRequest(
            id = UUID.randomUUID().toString(),
            fabricItem = fabricItem
        )
        _swapRequests.value = _swapRequests.value + request
    }

    fun updateSwapRequestStatus(requestId: String, newStatus: String) {
        _swapRequests.value = _swapRequests.value.map {
            if (it.id == requestId) it.copy(status = newStatus) else it
        }
    }
}
