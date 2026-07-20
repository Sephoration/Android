package com.example.loginapp.util

import java.security.MessageDigest

object PasswordHash {
    fun hash(password: String): String {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(password.toByteArray())
            hash.joinToString("") { String.format("%02x", it) }
        } catch (e: Exception) {
            password
        }
    }
}
