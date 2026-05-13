package com.example.kutirakone.model

data class SwapRequest(
    val id: String,
    val fabricItem: FabricItem,
    val status: String = "Pending", // Pending, Accepted, Declined
    val timestamp: Long = System.currentTimeMillis()
)