package com.example.taskmanager.data.repository

import com.example.taskmanager.data.local.TaskDao
import com.example.taskmanager.data.model.Task
import com.example.taskmanager.data.model.TaskWithCategory
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    val allTasksWithCategory: Flow<List<TaskWithCategory>> = taskDao.getAllTasksWithCategory()

    fun getTasksByCategory(categoryId: Long): Flow<List<TaskWithCategory>> =
        taskDao.getTasksByCategory(categoryId)

    fun getTasksByCompletion(isCompleted: Boolean): Flow<List<TaskWithCategory>> =
        taskDao.getTasksByCompletion(isCompleted)

    fun searchTasks(query: String): Flow<List<TaskWithCategory>> =
        taskDao.searchTasks(query)

    fun getTasksDueBetween(start: Long, end: Long): Flow<List<TaskWithCategory>> =
        taskDao.getTasksDueBetween(start, end)

    fun getOverdueTasks(now: Long): Flow<List<TaskWithCategory>> =
        taskDao.getOverdueTasks(now)

    fun getTaskWithCategory(taskId: Long): Flow<TaskWithCategory?> =
        taskDao.getTaskWithCategory(taskId)

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()

    val taskCount: Flow<Int> = taskDao.getTaskCount()

    val pendingCount: Flow<Int> = taskDao.getPendingCount()

    val completedCount: Flow<Int> = taskDao.getCompletedCount()

    fun getOverdueCount(now: Long): Flow<Int> = taskDao.getOverdueCount(now)
}
