package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.viewmodel.RoutineViewModel

@Composable
fun RoutineDetailsScreen(routineId: Int, viewModel: RoutineViewModel) {
    val routineState by viewModel.getRoutineById(routineId).collectAsState(initial = null)
    val exercisesState by viewModel.exercises.collectAsState()

    routineState?.let { routine ->
        val routineExercises = exercisesState.filter { it.id in routine.exerciseIds }
        RoutineDetails(routine, routineExercises)
    } ?: run {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Routine not found")
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
            Text(text = routine.name, style = androidx.compose.ui.text.TextStyle(fontSize = 24.sp))
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(exercises) { exercise ->
            ExerciseItem(exercise)
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Button(
                onClick = { /* TODO: Implement start workout functionality */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Workout")
            }
        }
    }
}

@Composable
fun ExerciseItem(exercise: Exercise) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = exercise.name)
    }
}