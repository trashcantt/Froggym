package com.sabdev.froggym.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sabdev.froggym.ui.screens.*
import com.sabdev.froggym.viewmodel.RoutineViewModel

@Composable
fun SetupNavigation(
    navController: NavHostController,
    routineViewModel: RoutineViewModel
) {
    NavHost(navController = navController, startDestination = "routineList") {
        composable("routineList") {
            RoutinesScreen(
                viewModel = routineViewModel,
                onCreateRoutine = { navController.navigate("createRoutine") },
                onRoutineSelected = { routineId ->
                    navController.navigate("routineDetails/$routineId")
                }
            )
        }
        composable("createRoutine") {
            CreateRoutineScreen(
                viewModel = routineViewModel,
                onRoutineCreated = { navController.popBackStack() }
            )
        }
        composable(
            "routineDetails/{routineId}",
            arguments = listOf(navArgument("routineId") { type = NavType.LongType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getLong("routineId") ?: return@composable
            RoutineDetailsScreen(
                routineId = routineId,
                viewModel = routineViewModel,
                onNavigateToAddExercises = { id ->
                    navController.navigate("addExercises/$id")
                }
            )
        }
        composable(
            "addExercises/{routineId}",
            arguments = listOf(navArgument("routineId") { type = NavType.LongType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getLong("routineId") ?: return@composable
            LaunchedEffect(routineId) {
                routineViewModel.getRoutineType(routineId)
            }
            val routineType by routineViewModel.routineType.collectAsState()
            routineType?.let { type ->
                AddExercisesScreen(
                    routineId = routineId,
                    routineType = type,
                    viewModel = routineViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}