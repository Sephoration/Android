package com.example.loginapp.data.repository

import com.example.loginapp.data.local.UserDao
import com.example.loginapp.data.model.User

class UserRepository(private val userDao: UserDao) {

    suspend fun login(username: String, password: String): User? {
        return userDao.login(username, password)
    }

    suspend fun register(username: String, password: String): RegistrationResult {
        val existingUser = userDao.getUserByUsername(username)
        return if (existingUser != null) {
            RegistrationResult.UsernameTaken
        } else {
            userDao.insert(User(username = username, password = password))
            RegistrationResult.Success
        }
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun deleteUser(user: User) {
        userDao.delete(user)
    }
}

sealed class RegistrationResult {
    data object Success : RegistrationResult()
    data object UsernameTaken : RegistrationResult()
}
