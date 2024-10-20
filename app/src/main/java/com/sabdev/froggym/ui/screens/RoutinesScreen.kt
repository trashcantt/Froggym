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
    onRoutineSelected: (Int) -> Unit
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
    onRoutineSelected: (Int) -> Unit,
    exerciseType: ExerciseType,
    isSelectionMode: Boolean,
    selectedRoutines: Set<Routine>,
    onSelectionModeChanged: (Boolean) -> Unit,
    onSelectedRoutinesChanged: (Set<Routine>) -> Unit
) {
    val routines by (if (exerciseType == ExerciseType.GYM) viewModel.gymRoutines else viewModel.calisthenicRoutines).collectAsState()

    RoutineList(
        routines = routines,
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
fun PredefinedRoutines(level: String, onRoutineSelected: (Int) -> Unit, exerciseType: ExerciseType) {
    val predefinedRoutines by remember(level, exerciseType) {
        derivedStateOf {
            when (level) {
                "PPL" -> listOf(
                    Routine(1, "Push", exerciseType, listOf(1, 2, 3)),
                    Routine(2, "Pull", exerciseType, listOf(4, 5, 6)),
                    Routine(3, "Legs", exerciseType, listOf(7, 8, 9))
                )
                "Arnold Split" -> listOf(
                    Routine(4, "Chest & Back", exerciseType, listOf(10, 11, 12)),
                    Routine(5, "Shoulders & Arms", exerciseType, listOf(13, 14, 15)),
                    Routine(6, "Legs", exerciseType, listOf(16, 17, 18))
                )
                "Heavy Duty" -> listOf(
                    Routine(7, "Full Body", exerciseType, listOf(19, 20, 21))
                )
                "Principiante" -> listOf(
                    Routine(8, "Rutina de fuerza para principiantes", exerciseType, listOf(22, 23, 24)),
                    Routine(9, "Rutina de cardio para principiantes", exerciseType, listOf(25, 26, 27))
                )
                "Intermedio" -> listOf(
                    Routine(10, "Rutina de hipertrofia intermedia", exerciseType, listOf(28, 29, 30)),
                    Routine(11, "Rutina de resistencia intermedia", exerciseType, listOf(31, 32, 33))
                )
                "Avanzado" -> listOf(
                    Routine(12, "Rutina de potencia avanzada", exerciseType, listOf(34, 35, 36)),
                    Routine(13, "Rutina de calistenia avanzada", exerciseType, listOf(37, 38, 39))
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
    onRoutineSelected: (Int) -> Unit,
    onSelectionChanged: (Routine, Boolean) -> Unit,
    onLongPress: (Routine) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
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
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = routine.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Ejercicios: ${routine.exerciseIds.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null
                )
            }
        }
    }
}