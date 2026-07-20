package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.local.AppDatabase
import com.example.taskmanager.data.model.Task
import com.example.taskmanager.data.model.TaskWithCategory
import com.example.taskmanager.data.repository.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class FilterOption(val label: String) {
    ALL("全部"),
    PENDING("未完成"),
    COMPLETED("已完成"),
    OVERDUE("已逾期")
}

data class TaskListState(
    val tasks: List<TaskWithCategory> = emptyList(),
    val searchQuery: String = "",
    val filterOption: FilterOption = FilterOption.ALL,
    val isLoading: Boolean = true
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    private val _searchQuery = MutableStateFlow("")
    private val _filterOption = MutableStateFlow(FilterOption.ALL)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TaskRepository(db.taskDao())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val listState: StateFlow<TaskListState> = combine(
        _searchQuery, _filterOption
    ) { q, f -> Pair(q, f) }
        .flatMapLatest { (query, filter) ->
            val flow = when {
                query.isNotBlank() -> repository.searchTasks(query)
                filter == FilterOption.ALL -> repository.allTasksWithCategory
                filter == FilterOption.PENDING -> repository.getTasksByCompletion(false)
                filter == FilterOption.COMPLETED -> repository.getTasksByCompletion(true)
                filter == FilterOption.OVERDUE -> repository.getOverdueTasks(System.currentTimeMillis())
                else -> repository.allTasksWithCategory
            }
            flow.map { tasks ->
                TaskListState(
                    tasks = tasks,
                    searchQuery = query,
                    filterOption = filter,
                    isLoading = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskListState())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(option: FilterOption) {
        _filterOption.value = option
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
}
