package com.example.taskmanager.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.taskmanager.data.model.Priority
import com.example.taskmanager.ui.theme.Green40
import com.example.taskmanager.ui.theme.Orange40
import com.example.taskmanager.ui.theme.Red40

@Composable
fun StatusChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PriorityChip(priority: Priority, modifier: Modifier = Modifier) {
    val (color, label) = when (priority) {
        Priority.LOW -> Green40 to "低"
        Priority.MEDIUM -> Color(0xFF1976D2) to "中"
        Priority.HIGH -> Orange40 to "高"
        Priority.URGENT -> Red40 to "紧急"
    }
    StatusChip(text = label, color = color, modifier = modifier)
}

@Composable
fun CompletionChip(isCompleted: Boolean, modifier: Modifier = Modifier) {
    if (isCompleted) {
        StatusChip(text = "已完成", color = Green40, modifier = modifier)
    } else {
        StatusChip(text = "进行中", color = Orange40, modifier = modifier)
    }
}
