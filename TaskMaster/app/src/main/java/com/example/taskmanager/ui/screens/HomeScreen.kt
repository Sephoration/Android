package com.example.taskmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.taskmanager.ui.components.TaskCard
import com.example.taskmanager.viewmodel.FilterOption
import com.example.taskmanager.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TaskViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    val state by viewModel.listState.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (showSearch) {
                SearchBar(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onSearch = {},
                    active = false,
                    onActiveChange = {},
                    placeholder = { Text("搜索任务...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            viewModel.setSearchQuery("")
                            showSearch = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "关闭搜索")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {}
            } else {
                TopAppBar(
                    title = { Text("任务管理") },
                    actions = {
                        IconButton(onClick = { showSearch = true }) {
                            Icon(Icons.Default.Search, contentDescription = "搜索")
                        }
                        IconButton(onClick = onNavigateToStatistics) {
                            Icon(Icons.Default.Star, contentDescription = "统计")
                        }
                    }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "添加任务")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            FilterRow(
                selected = state.filterOption,
                onSelect = { viewModel.setFilter(it) }
            )

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.searchQuery.isNotBlank()) "没有找到匹配的任务"
                        else "还没有任务，点击 + 添加",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.tasks,
                        key = { it.task.id }
                    ) { taskWithCategory ->
                        TaskCard(
                            task = taskWithCategory,
                            onToggleComplete = {
                                viewModel.toggleTaskCompletion(taskWithCategory.task)
                            },
                            onClick = { onNavigateToDetail(taskWithCategory.task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterRow(
    selected: FilterOption,
    onSelect: (FilterOption) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selected.ordinal,
        edgePadding = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        FilterOption.entries.forEach { option ->
            Tab(
                selected = selected == option,
                onClick = { onSelect(option) },
                text = {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
    HorizontalDivider()
}
