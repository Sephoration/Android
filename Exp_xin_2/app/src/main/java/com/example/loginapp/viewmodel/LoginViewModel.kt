package com.example.loginapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapp.data.model.User
import com.example.loginapp.data.repository.UserRepository
import kotlinx.coroutines.launch

sealed class LoginResult {
    data object Idle : LoginResult()
    data object Loading : LoginResult()
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var loginResult by mutableStateOf<LoginResult>(LoginResult.Idle)
        private set

    fun onUsernameChanged(value: String) {
        username = value
        if (loginResult !is LoginResult.Idle) {
            loginResult = LoginResult.Idle
        }
    }

    fun onPasswordChanged(value: String) {
        password = value
        if (loginResult !is LoginResult.Idle) {
            loginResult = LoginResult.Idle
        }
    }

    fun login() {
        if (username.isBlank() || password.isBlank()) {
            loginResult = LoginResult.Error("用户名和密码不能为空")
            return
        }

        loginResult = LoginResult.Loading
        viewModelScope.launch {
            try {
                val user = repository.login(username.trim(), password)
                if (user != null) {
                    loginResult = LoginResult.Success(user)
                } else {
                    loginResult = LoginResult.Error("用户名或密码错误")
                }
            } catch (e: Exception) {
                loginResult = LoginResult.Error("登录失败：${e.message}")
            }
        }
    }
}
