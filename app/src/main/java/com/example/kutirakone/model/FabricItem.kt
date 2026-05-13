package com.example.kutirakone.model

data class FabricItem(
    val id: String = "",
    val imageUrl: String = "",
    val materialType: String = "", // Silk / Cotton / Wool
    val size: String = "",
    val description: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
