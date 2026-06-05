package com.example.loginapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.loginapp.data.model.User
import com.example.loginapp.data.repository.UserRepository
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    user: User,
    repository: UserRepository,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var userToDelete by remember { mutableStateOf<User?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val isAdmin = user.username == "admin"

    // Load users
    LaunchedEffect(Unit) {
        if (isAdmin) {
            users = repository.getAllUsers()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Welcome header
        Text(
            text = if (isAdmin) "欢迎，管理员！👋" else "欢迎，${user.username}！👋",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "您已成功登录。",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Admin panel - user management
        if (isAdmin) {
            Text(
                text = "用户管理",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                if (users.isEmpty()) {
                    Text(
                        text = "没有找到用户。",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    LazyColumn {
                        items(users) { u ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = u.username,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = if (u.username == "admin") "管理员" else "用户",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (u.username != "admin") {
                                    IconButton(onClick = { userToDelete = u }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete ${u.username}",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                            if (u != users.last()) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("退出登录")
        }
    }

    // Delete confirmation dialog
    if (userToDelete != null) {
        AlertDialog(
            onDismissRequest = { userToDelete = null },
            title = { Text("删除用户") },
            text = { Text("确定要删除用户 \"${userToDelete!!.username}\" 吗？") },
            confirmButton = {
                TextButton(onClick = {
                    val u = userToDelete!!
                    userToDelete = null
                    coroutineScope.launch {
                        repository.deleteUser(u)
                        users = repository.getAllUsers()
                    }
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { userToDelete = null }) {
                    Text("取消")
                }
            }
        )
    }
}
