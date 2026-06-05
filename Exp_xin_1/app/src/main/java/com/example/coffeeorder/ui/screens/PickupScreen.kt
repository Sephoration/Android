package com.example.coffeeorder.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coffeeorder.CupcakeViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PickupScreen(
    viewModel: CupcakeViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState

    // Generate next 7 days as date options
    val dateOptions = (0..6).map { daysFromNow ->
        val date = LocalDate.now().plusDays(daysFromNow.toLong())
        val formatter = DateTimeFormatter.ofPattern("EEE, MMM d")
        date.format(formatter) to date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "选择取货日期",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        dateOptions.forEach { (displayDate, isoDate) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = uiState.pickupDate == isoDate,
                    onClick = { viewModel.setPickupDate(isoDate) }
                )
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNext,
            enabled = uiState.pickupDate.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("下一步")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回")
        }
    }
}
