package com.example.loginapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.loginapp.data.model.User
import com.example.loginapp.data.repository.UserRepository
import com.example.loginapp.ui.screens.LoginScreen
import com.example.loginapp.ui.screens.MainScreen
import com.example.loginapp.ui.screens.RegisterScreen
import com.example.loginapp.viewmodel.LoginViewModel
import com.example.loginapp.viewmodel.RegisterViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val MAIN = "main"
}

@Composable
fun LoginNavGraph(
    navController: NavHostController,
    repository: UserRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(repository)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    val user = (loginViewModel.loginResult as com.example.loginapp.viewmodel.LoginResult.Success).user
                    navController.navigate("${Routes.MAIN}/${user.id}/${user.username}") {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            val registerViewModel: RegisterViewModel = viewModel(
                factory = RegisterViewModelFactory(repository)
            )
            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("${Routes.MAIN}/{userId}/{username}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: 0
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val user = User(id = userId, username = username, password = "")
            MainScreen(
                user = user,
                repository = repository,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}
