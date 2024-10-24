package com.sabdev.froggym.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.sabdev.froggym.data.entities.*
import com.sabdev.froggym.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    viewModel: RoutineViewModel,
    onCreateRoutine: () -> Unit,
    onRoutineSelected: (Long) -> Unit
) {
    var selectedType by remember { mutableStateOf(ExerciseType.GYM) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedRoutines by remember { mutableStateOf(setOf<Routine>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutinas", style = MaterialTheme.typography.headlineLarge) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isSelectionMode) {
                        viewModel.deleteRoutines(selectedRoutines.toList())
                        isSelectionMode = false
                        selectedRoutines = emptySet()
                    } else {
                        onCreateRoutine()
                    }
                }
            ) {
                Icon(
                    imageVector = if (isSelectionMode) Icons.Default.Delete else Icons.Default.Add,
                    contentDescription = if (isSelectionMode) "Eliminar rutinas seleccionadas" else "Crear rutina"
                )
            }
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
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { selectedType = ExerciseType.GYM },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == ExerciseType.GYM) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Gimnasio")
                }
                Button(
                    onClick = { selectedType = ExerciseType.CALISTHENICS },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == ExerciseType.CALISTHENICS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Calistenia")
                }
            }
            UserRoutines(
                viewModel = viewModel,
                onRoutineSelected = onRoutineSelected,
                exerciseType = selectedType,
                isSelectionMode = isSelectionMode,
                selectedRoutines = selectedRoutines,
                onSelectionModeChanged = { isSelectionMode = it },
                onSelectedRoutinesChanged = { selectedRoutines = it }
            )
        }
    }
}

@Composable
fun UserRoutines(
    viewModel: RoutineViewModel,
    onRoutineSelected: (Long) -> Unit,
    exerciseType: ExerciseType,
    isSelectionMode: Boolean,
    selectedRoutines: Set<Routine>,
    onSelectionModeChanged: (Boolean) -> Unit,
    onSelectedRoutinesChanged: (Set<Routine>) -> Unit
) {
    val userRoutines by when (exerciseType) {
        ExerciseType.GYM -> viewModel.gymRoutines.collectAsState(initial = emptyList())
        ExerciseType.CALISTHENICS -> viewModel.calisthenicRoutines.collectAsState(initial = emptyList())
    }

    RoutineList(
        routines = userRoutines,
        selectedRoutines = selectedRoutines,
        isSelectionMode = isSelectionMode,
        onRoutineSelected = onRoutineSelected,
        onSelectionChanged = { routine, selected ->
            val newSelectedRoutines = if (selected) {
                selectedRoutines + routine
            } else {
                selectedRoutines - routine
            }
            onSelectedRoutinesChanged(newSelectedRoutines)
            if (newSelectedRoutines.isEmpty()) {
                onSelectionModeChanged(false)
            }
        },
        onLongPress = { routine ->
            if (!isSelectionMode) {
                onSelectionModeChanged(true)
                onSelectedRoutinesChanged(setOf(routine))
            }
        }
    )
}

@Composable
fun RoutineList(
    routines: List<Routine>,
    selectedRoutines: Set<Routine>,
    isSelectionMode: Boolean,
    onRoutineSelected: (Long) -> Unit,
    onSelectionChanged: (Routine, Boolean) -> Unit,
    onLongPress: (Routine) -> Unit
) {
    LazyColumn {
        items(routines) { routine ->
            RoutineItem(
                routine = routine,
                isSelected = selectedRoutines.contains(routine),
                isSelectionMode = isSelectionMode,
                onClick = {
                    if (isSelectionMode) {
                        onSelectionChanged(routine, !selectedRoutines.contains(routine))
                    } else {
                        onRoutineSelected(routine.id)
                    }
                },
                onLongClick = { onLongPress(routine) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoutineItem(
    routine: Routine,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isSelected)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
            Column {
                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Type: ${routine.type}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}