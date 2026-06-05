package com.example.coffeeorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.coffeeorder.navigation.CoffeeNavGraph
import com.example.coffeeorder.ui.theme.CoffeeOrderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoffeeOrderTheme {
                Surface(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    val navController = rememberNavController()
                    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<CupcakeViewModel>()
                    CoffeeNavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
