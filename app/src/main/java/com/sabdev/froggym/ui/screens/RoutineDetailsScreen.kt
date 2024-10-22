package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.viewmodel.RoutineViewModel

@Composable
fun RoutineDetailsScreen(
    routineId: Long,
    viewModel: RoutineViewModel
) {
    LaunchedEffect(routineId) {
        viewModel.loadRoutineWithExercises(routineId)
    }

    val routineWithExercises by viewModel.selectedRoutine.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    when {
        routineWithExercises != null -> {
            RoutineDetails(
                routine = routineWithExercises!!.routine,
                exercises = routineWithExercises!!.exercises
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

@Composable
fun RoutineDetails(routine: Routine, exercises: List<Exercise>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = routine.name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Type: ${routine.type}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(exercises) { exercise ->
            ExerciseItem(exercise)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
        }

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

@Composable
fun ExerciseItem(exercise: Exercise) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Description: ${exercise.description}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}