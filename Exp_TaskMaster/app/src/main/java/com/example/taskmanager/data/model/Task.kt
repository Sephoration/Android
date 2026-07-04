package com.example.taskmanager.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val categoryId: Long? = null,
    val isCompleted: Boolean = false,
    val priority: Int = 1,
    val dueDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Priority(val level: Int, val label: String) {
    LOW(0, "低"),
    MEDIUM(1, "中"),
    HIGH(2, "高"),
    URGENT(3, "紧急");

    companion object {
        fun fromLevel(level: Int): Priority =
            entries.firstOrNull { it.level == level } ?: MEDIUM
    }
}
