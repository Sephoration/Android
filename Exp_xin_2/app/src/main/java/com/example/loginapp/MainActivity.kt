package com.example.loginapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.loginapp.data.local.AppDatabase
import com.example.loginapp.data.repository.UserRepository
import com.example.loginapp.navigation.LoginNavGraph
import com.example.loginapp.ui.theme.LoginAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and repository
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UserRepository(database.userDao())

        enableEdgeToEdge()
        setContent {
            LoginAppTheme {
                Surface(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
                    val navController = rememberNavController()
                    LoginNavGraph(
                        navController = navController,
                        repository = repository
                    )
                }
            }
        }
    }
}
