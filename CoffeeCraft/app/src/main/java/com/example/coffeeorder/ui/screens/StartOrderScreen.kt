package com.example.coffeeorder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coffeeorder.CupcakeViewModel

@Composable
fun StartOrderScreen(
    viewModel: CupcakeViewModel,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "☕ 咖啡订购",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "您想要几杯咖啡？",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Quantity buttons: 1-5 in first row, 6-10 in second row
        val quantities = (1..10).toList()
        quantities.chunked(5).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { qty ->
                    val isSelected = uiState.quantity == qty
                    if (isSelected) {
                        Button(
                            onClick = { viewModel.setQuantity(qty) },
                            modifier = Modifier.weight(1f).padding(4.dp)
                        ) {
                            Text(text = qty.toString())
                        }
                    } else {
                        FilledTonalButton(
                            onClick = { viewModel.setQuantity(qty) },
                            modifier = Modifier.weight(1f).padding(4.dp)
                        ) {
                            Text(text = qty.toString())
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            enabled = uiState.quantity > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("下一步")
        }
    }
}
