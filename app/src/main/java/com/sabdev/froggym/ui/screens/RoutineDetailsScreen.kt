package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineDetailsScreen(
    routineId: Long,
    viewModel: RoutineViewModel,
    onNavigateToAddExercises: (Long) -> Unit
) {
    LaunchedEffect(routineId) {
        viewModel.loadRoutineWithExercises(routineId)
    }

    val routineWithExercises by viewModel.selectedRoutine.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var editedRoutineName by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isEditing) {
                        // Save changes
                        viewModel.updateRoutine(routineId, editedRoutineName)
                        isEditing = false
                    } else {
                        // Enter edit mode
                        isEditing = true
                        editedRoutineName = routineWithExercises?.routine?.name ?: ""
                    }
                }
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                    contentDescription = if (isEditing) "Save" else "Edit"
                )
            }
        }
    ) { paddingValues ->
        when {
            routineWithExercises != null -> {
                RoutineDetails(
                    routine = routineWithExercises!!.routine,
                    exercises = routineWithExercises!!.exercises,
                    isEditing = isEditing,
                    editedRoutineName = editedRoutineName,
                    onRoutineNameChange = { editedRoutineName = it },
                    onAddExercise = { onNavigateToAddExercises(routineId) },
                    onRemoveExercise = { viewModel.removeExerciseFromRoutine(routineId, it) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error loading routine details")
                }
            }
        }
    }
}

@Composable
fun RoutineDetails(
    routine: Routine,
    exercises: List<Exercise>,
    isEditing: Boolean,
    editedRoutineName: String,
    onRoutineNameChange: (String) -> Unit,
    onAddExercise: () -> Unit,
    onRemoveExercise: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            if (isEditing) {
                OutlinedTextField(
                    value = editedRoutineName,
                    onValueChange = onRoutineNameChange,
                    label = { Text("Routine Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Text(
                text = "Type: ${routine.type}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(exercises) { exercise ->
            ExerciseItem(
                exercise = exercise,
                isEditing = isEditing,
                onRemove = { onRemoveExercise(exercise) }
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

        if (isEditing) {
            item {
                Button(
                    onClick = onAddExercise,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Add Exercise")
                }
            }
        }

        if (!isEditing) {
            item {
                Button(
                    onClick = { /* TODO: Implement start workout functionality */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Start Workout")
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    isEditing: Boolean,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { if (!isEditing) expanded = !expanded }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Description: ${exercise.description}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            if (isEditing) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Exercise")
                }
            }
        }
    }
}