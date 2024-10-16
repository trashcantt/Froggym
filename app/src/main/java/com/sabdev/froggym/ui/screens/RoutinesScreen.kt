package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sabdev.froggym.data.entities.ExerciseType
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    viewModel: RoutineViewModel,
    onCreateRoutine: () -> Unit
) {
    var selectedType by remember { mutableStateOf(ExerciseType.GYM) }
    val routines by (if (selectedType == ExerciseType.GYM) viewModel.gymRoutines else viewModel.calisthenicRoutines).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Rutinas") },
                actions = {
                    TextButton(onClick = onCreateRoutine) {
                        Text("Crear Rutina")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(routines) { routine ->
                    RoutineItem(routine = routine)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineItem(routine: Routine) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = routine.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = "Ejercicios: ${routine.exerciseIds.size}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}