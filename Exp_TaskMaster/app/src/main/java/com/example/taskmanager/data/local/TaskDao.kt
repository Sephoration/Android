package com.example.taskmanager.data.local

import androidx.room.*
import com.example.taskmanager.data.model.Task
import com.example.taskmanager.data.model.TaskWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY priority DESC, createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY priority DESC, createdAt DESC")
    fun getAllTasksWithCategory(): Flow<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY priority DESC, createdAt DESC")
    fun getTasksByCategory(categoryId: Long): Flow<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted ORDER BY priority DESC, createdAt DESC")
    fun getTasksByCompletion(isCompleted: Boolean): Flow<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY priority DESC, createdAt DESC")
    fun searchTasks(query: String): Flow<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate BETWEEN :start AND :end ORDER BY dueDate ASC")
    fun getTasksDueBetween(start: Long, end: Long): Flow<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE dueDate IS NOT NULL AND dueDate < :now AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getOverdueTasks(now: Long): Flow<List<TaskWithCategory>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskWithCategory(taskId: Long): Flow<TaskWithCategory?>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate IS NOT NULL AND dueDate < :now AND isCompleted = 0")
    fun getOverdueCount(now: Long): Flow<Int>
}
