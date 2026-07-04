package com.example.loginapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapp.data.repository.RegistrationResult
import com.example.loginapp.data.repository.UserRepository
import kotlinx.coroutines.launch

sealed class RegisterResult {
    data object Idle : RegisterResult()
    data object Loading : RegisterResult()
    data object Success : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}

class RegisterViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var username by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var confirmPassword by mutableStateOf("")
        private set

    var registerResult by mutableStateOf<RegisterResult>(RegisterResult.Idle)
        private set

    fun onUsernameChanged(value: String) {
        username = value
        if (registerResult !is RegisterResult.Idle) {
            registerResult = RegisterResult.Idle
        }
    }

    fun onPasswordChanged(value: String) {
        password = value
        if (registerResult !is RegisterResult.Idle) {
            registerResult = RegisterResult.Idle
        }
    }

    fun onConfirmPasswordChanged(value: String) {
        confirmPassword = value
        if (registerResult !is RegisterResult.Idle) {
            registerResult = RegisterResult.Idle
        }
    }

    fun register() {
        // Validation
        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            registerResult = RegisterResult.Error("所有字段必填")
            return
        }

        if (username.trim().length < 3) {
            registerResult = RegisterResult.Error("用户名至少3个字符")
            return
        }

        if (password.length < 6) {
            registerResult = RegisterResult.Error("密码至少6个字符")
            return
        }

        if (password != confirmPassword) {
            registerResult = RegisterResult.Error("两次密码不一致")
            return
        }

        registerResult = RegisterResult.Loading
        viewModelScope.launch {
            try {
                when (repository.register(username.trim(), password)) {
                    is RegistrationResult.Success -> {
                        registerResult = RegisterResult.Success
                    }
                    is RegistrationResult.UsernameTaken -> {
                        registerResult = RegisterResult.Error("用户名已存在")
                    }
                }
            } catch (e: Exception) {
                registerResult = RegisterResult.Error("注册失败：${e.message}")
            }
        }
    }
}
