package com.example.taskmanager.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.model.TaskWithCategory
import com.example.taskmanager.ui.theme.Green40
import com.example.taskmanager.ui.theme.Grey40
import com.example.taskmanager.ui.theme.Red40
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskCard(
    task: TaskWithCategory,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val formattedDate = task.task.dueDate?.let { millis ->
        val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
        sdf.format(Date(millis))
    }

    val isOverdue = task.task.dueDate != null &&
            task.task.dueDate < System.currentTimeMillis() &&
            !task.task.isCompleted

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = onToggleComplete) {
                Icon(
                    imageVector = if (task.task.isCompleted)
                        Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (task.task.isCompleted) "标记未完成" else "标记完成",
                    tint = if (task.task.isCompleted) Green40 else Grey40,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.task.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    textDecoration = if (task.task.isCompleted)
                        TextDecoration.LineThrough else TextDecoration.None,
                    color = if (task.task.isCompleted)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.onSurface
                )

                if (task.task.description.isNotBlank()) {
                    Text(
                        text = task.task.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PriorityChip(priority = task.task.priority.toPriority())

                    if (task.category != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusChip(
                            text = task.category.name,
                            color = Color(task.category.color)
                        )
                    }

                    if (formattedDate != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        StatusChip(
                            text = formattedDate,
                            color = if (isOverdue) Red40 else Grey40
                        )
                    }
                }
            }
        }
    }
}

private fun Int.toPriority() = com.example.taskmanager.data.model.Priority.fromLevel(this)
