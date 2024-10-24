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
    var selectedTabIndex by remember { mutableStateOf(0) }
    val gymTabs = listOf("Mis Rutinas", "PPL", "Arnold Split", "Heavy Duty")
    val calistheniasTabs = listOf("Mis Rutinas", "Principiante", "Intermedio", "Avanzado")
    val tabs = if (selectedType == ExerciseType.GYM) gymTabs else calistheniasTabs
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedRoutines by remember { mutableStateOf(setOf<Routine>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutinas", style = MaterialTheme.typography.headlineLarge) }
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 0) {
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
                    onClick = {
                        selectedType = ExerciseType.GYM
                        selectedTabIndex = 0
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == ExerciseType.GYM) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Gimnasio")
                }
                Button(
                    onClick = {
                        selectedType = ExerciseType.CALISTHENICS
                        selectedTabIndex = 0
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedType == ExerciseType.CALISTHENICS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Calistenia")
                }
            }
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            when {
                selectedTabIndex == 0 -> UserRoutines(
                    viewModel = viewModel,
                    onRoutineSelected = onRoutineSelected,
                    exerciseType = selectedType,
                    isSelectionMode = isSelectionMode,
                    selectedRoutines = selectedRoutines,
                    onSelectionModeChanged = { isSelectionMode = it },
                    onSelectedRoutinesChanged = { selectedRoutines = it }
                )
                else -> PredefinedRoutines(tabs[selectedTabIndex], onRoutineSelected, selectedType)
            }
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
fun PredefinedRoutines(
    level: String,
    onRoutineSelected: (Long) -> Unit,
    exerciseType: ExerciseType
) {
    val predefinedRoutines by remember {
        derivedStateOf {
            when (level) {
                "PPL" -> listOf(
                    Routine(id = 10001, name = "Push", type = exerciseType),
                    Routine(id = 10002, name = "Pull", type = exerciseType),
                    Routine(id = 10003, name = "Legs", type = exerciseType)
                )
                "Arnold Split" -> listOf(
                    Routine(id = 10004, name = "Chest & Back", type = exerciseType),
                    Routine(id = 10005, name = "Shoulders & Arms", type = exerciseType),
                    Routine(id = 10006, name = "Legs", type = exerciseType)
                )
                "Heavy Duty" -> listOf(
                    Routine(id = 10007, name = "Full Body", type = exerciseType)
                )
                "Principiante" -> listOf(
                    Routine(id = 10008, name = "Rutina de fuerza para principiantes", type = exerciseType),
                    Routine(id = 10009, name = "Rutina de cardio para principiantes", type = exerciseType)
                )
                "Intermedio" -> listOf(
                    Routine(id = 10010, name = "Rutina de hipertrofia intermedia", type = exerciseType),
                    Routine(id = 10011, name = "Rutina de resistencia intermedia", type = exerciseType)
                )
                "Avanzado" -> listOf(
                    Routine(id = 10012, name = "Rutina de fuerza avanzada", type = exerciseType),
                    Routine(id = 10013, name = "Rutina de skill avanzada", type = exerciseType)
                )
                else -> emptyList()
            }
        }
    }

    RoutineList(
        routines = predefinedRoutines,
        selectedRoutines = emptySet(),
        isSelectionMode = false,
        onRoutineSelected = onRoutineSelected,
        onSelectionChanged = { _, _ -> },
        onLongPress = { }
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