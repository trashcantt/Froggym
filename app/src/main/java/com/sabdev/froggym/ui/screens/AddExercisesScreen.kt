package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.ExerciseType
import com.sabdev.froggym.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExercisesScreen(
    viewModel: RoutineViewModel,
    routineId: Long,
    routineType: ExerciseType,
    onNavigateBack: (Boolean) -> Unit
) {
    val exercises by viewModel.exercises.collectAsState()
    var selectedExercises by remember { mutableStateOf(setOf<Long>()) }

    val filteredExercises = exercises.filter { it.type == routineType }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Ejercicios") },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack(false) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        selectedExercises.forEach { exerciseId ->
                            filteredExercises.find { it.id == exerciseId }?.let { exercise ->
                                viewModel.addExerciseToRoutine(routineId, exercise)
                            }
                        }
                        onNavigateBack(true)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Agregar Ejercicios Seleccionados")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(filteredExercises) { exercise ->
                ExerciseItem(
                    exercise = exercise,
                    isSelected = selectedExercises.contains(exercise.id),
                    onToggleSelection = {
                        selectedExercises = if (it) {
                            selectedExercises + exercise.id
                        } else {
                            selectedExercises - exercise.id
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    isSelected: Boolean,
    onToggleSelection: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onToggleSelection
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = exercise.name,
            modifier = Modifier.weight(1f)
        )
    }
}