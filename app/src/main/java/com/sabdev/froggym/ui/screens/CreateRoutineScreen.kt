package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.ExerciseType
import com.sabdev.froggym.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    viewModel: RoutineViewModel,
    onRoutineCreated: () -> Unit
) {
    var routineName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ExerciseType.GYM) }
    var selectedExercises by remember { mutableStateOf(setOf<Int>()) }

    val exercises by viewModel.getExercisesByType(selectedType).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Rutina") },
                actions = {
                    TextButton(
                        onClick = {
                            if (routineName.isNotBlank() && selectedExercises.isNotEmpty()) {
                                viewModel.createRoutine(routineName, selectedType, selectedExercises.toList())
                                onRoutineCreated()
                            }
                        }
                    ) {
                        Text("Guardar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = routineName,
                onValueChange = { routineName = it },
                label = { Text("Nombre de la rutina") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { selectedType = ExerciseType.GYM },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == ExerciseType.GYM) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Gimnasio")
                }
                Button(
                    onClick = { selectedType = ExerciseType.CALISTHENICS },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == ExerciseType.CALISTHENICS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Calistenia")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(exercises) { exercise ->
                    ExerciseItem(
                        exercise = exercise,
                        isSelected = selectedExercises.contains(exercise.id),
                        onSelectionChanged = { isSelected ->
                            selectedExercises = if (isSelected) {
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
}

@Composable
fun ExerciseItem(
    exercise: Exercise,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = exercise.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = exercise.description, style = MaterialTheme.typography.bodyMedium)
        }
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectionChanged
        )
    }
}