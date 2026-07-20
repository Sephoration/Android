package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.local.AppDatabase
import com.example.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StatisticsState(
    val totalCount: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val overdueCount: Int = 0,
    val completionRate: Float = 0f,
    val isLoading: Boolean = true
)

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    val state: StateFlow<StatisticsState>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao())

        state = combine(
            repository.taskCount,
            repository.pendingCount,
            repository.completedCount,
            repository.getOverdueCount(System.currentTimeMillis())
        ) { total, pending, completed, overdue ->
            val rate = if (total > 0) completed.toFloat() / total else 0f
            StatisticsState(
                totalCount = total,
                pendingCount = pending,
                completedCount = completed,
                overdueCount = overdue,
                completionRate = rate,
                isLoading = false
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatisticsState())
    }

    fun refresh() {
        viewModelScope.launch {
            state.value
        }
    }
}
