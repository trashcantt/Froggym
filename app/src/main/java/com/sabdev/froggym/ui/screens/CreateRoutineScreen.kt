package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.ExerciseType
import com.sabdev.froggym.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(viewModel: RoutineViewModel, onRoutineCreated: () -> Unit) {
    var routineName by remember { mutableStateOf("") }
    var isGymRoutine by remember { mutableStateOf(true) }
    var selectedExercises by remember { mutableStateOf(listOf<Int>()) }

    val exercises by (if (isGymRoutine) viewModel.gymExercises else viewModel.calisthenicExercises)
        .collectAsState(initial = emptyList())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
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
                    onClick = { isGymRoutine = true },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text("Gimnasio")
                }
                Button(
                    onClick = { isGymRoutine = false },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text("Calistenia")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExerciseList(
                exercises = exercises,
                selectedExercises = selectedExercises,
                onSelectionChanged = { selectedExercises = it }
            )
        }

        FloatingActionButton(
            onClick = {
                if (routineName.isNotBlank() && selectedExercises.isNotEmpty()) {
                    viewModel.createRoutine(
                        name = routineName,
                        type = if (isGymRoutine) ExerciseType.GYM else ExerciseType.CALISTHENICS,
                        exerciseIds = selectedExercises
                    )
                    onRoutineCreated()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Crear Rutina")
        }
    }
}

@Composable
fun ExerciseList(
    exercises: List<Exercise>,
    selectedExercises: List<Int>,
    onSelectionChanged: (List<Int>) -> Unit
) {
    LazyColumn {
        items(exercises) { exercise ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = selectedExercises.contains(exercise.id),
                    onCheckedChange = { isChecked ->
                        val newSelection = if (isChecked) {
                            selectedExercises + exercise.id
                        } else {
                            selectedExercises - exercise.id
                        }
                        onSelectionChanged(newSelection)
                    }
                )
                Text(text = exercise.name, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}