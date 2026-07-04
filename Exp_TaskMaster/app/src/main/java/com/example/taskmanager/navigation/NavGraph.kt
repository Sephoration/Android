package com.example.taskmanager.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.taskmanager.ui.screens.*
import com.example.taskmanager.viewmodel.CategoryViewModel
import com.example.taskmanager.viewmodel.StatisticsViewModel
import com.example.taskmanager.viewmodel.TaskViewModel

object Routes {
    const val HOME = "home"
    const val ADD_TASK = "add_task"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val TASK_DETAIL = "task_detail/{taskId}"
    const val CATEGORIES = "categories"
    const val STATISTICS = "statistics"

    fun taskDetail(id: Long) = "task_detail/$id"
    fun editTask(id: Long) = "edit_task/$id"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    taskViewModel: TaskViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    statisticsViewModel: StatisticsViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = taskViewModel,
                onNavigateToDetail = { taskId ->
                    navController.navigate(Routes.taskDetail(taskId))
                },
                onNavigateToAdd = {
                    navController.navigate(Routes.ADD_TASK)
                },
                onNavigateToStatistics = {
                    navController.navigate(Routes.STATISTICS)
                }
            )
        }

        composable(Routes.ADD_TASK) {
            AddEditTaskScreen(
                taskId = null,
                taskViewModel = taskViewModel,
                categoryViewModel = categoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.EDIT_TASK,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            AddEditTaskScreen(
                taskId = taskId,
                taskViewModel = taskViewModel,
                categoryViewModel = categoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.TASK_DETAIL,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            TaskDetailScreen(
                taskId = taskId,
                taskViewModel = taskViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CATEGORIES) {
            CategoriesScreen(
                categoryViewModel = categoryViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.STATISTICS) {
            StatisticsScreen(
                viewModel = statisticsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
