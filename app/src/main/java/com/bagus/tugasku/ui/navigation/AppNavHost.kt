package com.bagus.tugasku.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bagus.tugasku.ui.detail.TaskDetailScreen
import com.bagus.tugasku.ui.form.TaskFormScreen
import com.bagus.tugasku.ui.list.TaskListScreen

/**
 * Graf navigasi 4 layar:
 *   Daftar -> Tambah -> (Simpan) -> Daftar
 *   Daftar -> Detail -> Edit / Hapus -> Daftar
 * Layar Tambah dan Edit memakai satu composable (TaskFormScreen) dengan route berbeda.
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LIST,
        modifier = modifier,
    ) {
        composable(Routes.LIST) {
            TaskListScreen(
                onAddTask = { navController.navigate(Routes.form()) },
                onTaskClick = { id -> navController.navigate(Routes.detail(id)) },
            )
        }

        composable(
            route = Routes.FORM_PATTERN,
            arguments = listOf(
                navArgument(TASK_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = NO_TASK_ID
                },
            ),
        ) {
            TaskFormScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route = Routes.DETAIL_PATTERN,
            arguments = listOf(navArgument(TASK_ID_ARG) { type = NavType.LongType }),
        ) {
            TaskDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onEditTask = { id -> navController.navigate(Routes.form(id)) },
            )
        }
    }
}
