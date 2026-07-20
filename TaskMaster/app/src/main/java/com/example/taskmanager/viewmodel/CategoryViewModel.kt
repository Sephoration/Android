package com.example.taskmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.data.local.AppDatabase
import com.example.taskmanager.data.model.Category
import com.example.taskmanager.data.repository.CategoryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoryListState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true
)

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoryRepository

    val listState: StateFlow<CategoryListState>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = CategoryRepository(db.categoryDao())

        listState = repository.allCategories
            .map { categories ->
                CategoryListState(categories = categories, isLoading = false)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CategoryListState())
    }

    fun insertCategory(name: String, color: Long, icon: String = "folder") {
        viewModelScope.launch {
            repository.insertCategory(Category(name = name, color = color, icon = icon))
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }
}
