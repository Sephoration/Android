package com.example.taskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.model.Priority
import com.example.taskmanager.data.model.TaskWithCategory
import com.example.taskmanager.ui.components.CompletionChip
import com.example.taskmanager.ui.components.PriorityChip
import com.example.taskmanager.ui.components.StatusChip
import com.example.taskmanager.ui.theme.Red40
import com.example.taskmanager.viewmodel.TaskViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Long,
    taskViewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val listState by taskViewModel.listState.collectAsStateWithLifecycle()
    val taskWithCategory = remember(listState.tasks, taskId) {
        listState.tasks.find { it.task.id == taskId }
    }

    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这个任务吗？此操作无法撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskWithCategory?.let {
                            scope.launch {
                                taskViewModel.deleteTask(it.task)
                                onNavigateBack()
                            }
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("任务详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "删除",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (taskWithCategory == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            taskWithCategory?.let { twc ->
                DetailContent(
                    taskWithCategory = twc,
                    onToggleComplete = {
                        taskViewModel.toggleTaskCompletion(twc.task)
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    taskWithCategory: TaskWithCategory,
    onToggleComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val task = taskWithCategory.task

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineMedium,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isCompleted)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CompletionChip(isCompleted = task.isCompleted)
            PriorityChip(priority = Priority.fromLevel(task.priority))
            if (taskWithCategory.category != null) {
                StatusChip(
                    text = taskWithCategory.category.name,
                    color = Color(taskWithCategory.category.color)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (task.description.isNotBlank()) {
            Text(
                text = "描述",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        DetailRow(
            icon = Icons.Default.DateRange,
            label = "截止日期",
            value = task.dueDate?.let {
                SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault()).format(Date(it))
            } ?: "未设置"
        )

        if (task.dueDate != null) {
            val isOverdue = task.dueDate < System.currentTimeMillis() && !task.isCompleted
            if (isOverdue) {
                Text(
                    text = "已逾期！",
                    color = Red40,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 40.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        DetailRow(
            icon = Icons.Default.CalendarToday,
            label = "创建时间",
            value = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault())
                .format(Date(task.createdAt))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onToggleComplete,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (task.isCompleted)
                    MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = if (task.isCompleted)
                    Icons.AutoMirrored.Filled.Undo else Icons.Default.CheckCircle,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (task.isCompleted) "标记为未完成" else "标记为已完成")
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
