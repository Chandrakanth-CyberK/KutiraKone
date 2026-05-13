package com.example.kutirakone.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.kutirakone.viewmodel.FabricViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapRequestsScreen(
    viewModel: FabricViewModel,
    onNavigateBack: () -> Unit
) {
    val requests by viewModel.swapRequests.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Swap Requests") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (requests.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No swap requests yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(requests) { request ->
                    SwapRequestCard(
                        request = request,
                        onAccept = { viewModel.updateSwapRequestStatus(request.id, "Accepted") },
                        onDecline = { viewModel.updateSwapRequestStatus(request.id, "Declined") }
                    )
                }
            }
        }
    }
}

@Composable
fun SwapRequestCard(
    request: com.example.kutirakone.model.SwapRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Request for: ${request.fabricItem.materialType}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(text = "Status: ${request.status}", color = when(request.status) {
                "Accepted" -> MaterialTheme.colorScheme.primary
                "Declined" -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.secondary
            })
            
            if (request.status == "Pending") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDecline) {
                        Icon(Icons.Default.Close, contentDescription = "Decline", tint = MaterialTheme.colorScheme.error)
                    }
                    IconButton(onClick = onAccept) {
                        Icon(Icons.Default.Check, contentDescription = "Accept", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
