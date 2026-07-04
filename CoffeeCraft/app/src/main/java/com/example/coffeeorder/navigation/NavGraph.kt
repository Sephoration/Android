package com.example.coffeeorder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.coffeeorder.CupcakeViewModel
import com.example.coffeeorder.ui.screens.FlavorSelectionScreen
import com.example.coffeeorder.ui.screens.PickupScreen
import com.example.coffeeorder.ui.screens.StartOrderScreen
import com.example.coffeeorder.ui.screens.SummaryScreen
import com.example.coffeeorder.ui.screens.TemperatureSelectionScreen

object Routes {
    const val START = "start"
    const val FLAVOR = "flavor"
    const val TEMPERATURE = "temperature"
    const val PICKUP = "pickup"
    const val SUMMARY = "summary"
}

@Composable
fun CoffeeNavGraph(
    navController: NavHostController,
    viewModel: CupcakeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.START
    ) {
        composable(Routes.START) {
            StartOrderScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Routes.FLAVOR) }
            )
        }

        composable(Routes.FLAVOR) {
            FlavorSelectionScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Routes.TEMPERATURE) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.TEMPERATURE) {
            TemperatureSelectionScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Routes.PICKUP) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.PICKUP) {
            PickupScreen(
                viewModel = viewModel,
                onNext = { navController.navigate(Routes.SUMMARY) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SUMMARY) {
            SummaryScreen(
                viewModel = viewModel,
                onPlaceOrder = {
                    viewModel.resetOrder()
                    navController.popBackStack(Routes.START, inclusive = false)
                },
                onCancel = {
                    navController.popBackStack(Routes.START, inclusive = false)
                }
            )
        }
    }
}
