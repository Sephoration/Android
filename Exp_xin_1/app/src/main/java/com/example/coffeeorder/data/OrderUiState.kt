package com.example.coffeeorder.data

data class OrderUiState(
    val quantity: Int = 0,
    val flavor: String = "",
    val temperature: String = "",
    val pickupDate: String = ""
)
