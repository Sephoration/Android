package com.example.coffeeorder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.coffeeorder.data.OrderUiState
import java.text.NumberFormat

class CupcakeViewModel : ViewModel() {

    var uiState by mutableStateOf(OrderUiState())
        private set

    private val numberFormat: NumberFormat = NumberFormat.getCurrencyInstance()

    val price: String
        get() = numberFormat.format(calculatePrice())

    fun setQuantity(quantity: Int) {
        uiState = uiState.copy(quantity = quantity)
    }

    fun setFlavor(flavor: String) {
        uiState = uiState.copy(flavor = flavor)
    }

    fun setTemperature(temperature: String) {
        uiState = uiState.copy(temperature = temperature)
    }

    fun setPickupDate(date: String) {
        uiState = uiState.copy(pickupDate = date)
    }

    fun resetOrder() {
        uiState = OrderUiState()
    }

    private fun calculatePrice(): Double {
        val basePrice = uiState.quantity * 2.00
        val flavorUpcharge = if (uiState.flavor in listOf("Mocha", "Caramel")) {
            uiState.quantity * 1.00
        } else {
            0.00
        }
        return basePrice + flavorUpcharge
    }
}
