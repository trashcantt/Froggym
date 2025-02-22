package com.sabdev.froggym.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.ExerciseType
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.data.entities.RoutineWithExercises
import com.sabdev.froggym.data.repository.ExerciseRepository
import com.sabdev.froggym.data.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RoutineViewModel(
    private val routineRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _gymRoutines = MutableStateFlow<List<Routine>>(emptyList())
    val gymRoutines: StateFlow<List<Routine>> = _gymRoutines

    private val _calisthenicRoutines = MutableStateFlow<List<Routine>>(emptyList())
    val calisthenicRoutines: StateFlow<List<Routine>> = _calisthenicRoutines

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private val _gymExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val gymExercises: StateFlow<List<Exercise>> = _gymExercises

    private val _calisthenicExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val calisthenicExercises: StateFlow<List<Exercise>> = _calisthenicExercises

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedRoutine = MutableStateFlow<RoutineWithExercises?>(null)
    val selectedRoutine: StateFlow<RoutineWithExercises?> = _selectedRoutine

    private val _routineType = MutableStateFlow<ExerciseType?>(null)
    val routineType: StateFlow<ExerciseType?> = _routineType

    fun getRoutineType(routineId: Long) {
        viewModelScope.launch {
            routineRepository.getRoutineById(routineId).collect { routine ->
                _routineType.value = routine?.type
            }
        }
    }

    init {
        viewModelScope.launch {
            exerciseRepository.insertPredefinedExercises()
            loadRoutines()
            loadExercises()
            loadGymExercises()
            loadCalisthenicExercises()
        }
    }

    fun getRoutineById(id: Long): Flow<Routine?> {
        return routineRepository.getRoutineById(id)
    }

    private fun loadRoutines() {
        viewModelScope.launch {
            routineRepository.getRoutinesByType(ExerciseType.GYM).collect {
                _gymRoutines.value = it
            }
        }
        viewModelScope.launch {
            routineRepository.getRoutinesByType(ExerciseType.CALISTHENICS).collect {
                _calisthenicRoutines.value = it
            }
        }
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseRepository.allExercises.collect {
                _exercises.value = it
            }
        }
    }

    private fun loadGymExercises() {
        viewModelScope.launch {
            exerciseRepository.getExercisesByType(ExerciseType.GYM).collect {
                _gymExercises.value = it
            }
        }
    }

    private fun loadCalisthenicExercises() {
        viewModelScope.launch {
            exerciseRepository.getExercisesByType(ExerciseType.CALISTHENICS).collect {
                _calisthenicExercises.value = it
            }
        }
    }

    fun createRoutine(name: String, type: ExerciseType, exerciseIds: List<Long>) {
        viewModelScope.launch {
            val newRoutine = Routine(name = name, type = type)
            val routineId = routineRepository.insertRoutine(newRoutine)
            exerciseIds.forEach { exerciseId ->
                routineRepository.addExerciseToRoutine(routineId, exerciseId)
            }
        }
    }

    fun deleteRoutines(routines: List<Routine>) {
        viewModelScope.launch {
            routines.forEach { routine ->
                routineRepository.deleteRoutine(routine)
            }
        }
    }

    fun getExercisesByType(type: ExerciseType): StateFlow<List<Exercise>> {
        return when (type) {
            ExerciseType.GYM -> gymExercises
            ExerciseType.CALISTHENICS -> calisthenicExercises
        }
    }

    suspend fun getRoutineWithExercisesById(id: Long): RoutineWithExercises {
        return routineRepository.getRoutineWithExercisesById(id)
    }

    fun loadRoutineWithExercises(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val routine = routineRepository.getRoutineWithExercisesById(id)
                _selectedRoutine.value = routine
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateRoutine(routineId: Long, newName: String) {
        viewModelScope.launch {
            try {
                val existingRoutine = routineRepository.getRoutineById(routineId).first()
                if (existingRoutine != null) {
                    val updatedRoutine = existingRoutine.copy(name = newName)
                    routineRepository.updateRoutine(updatedRoutine)
                    // Refresh the selected routine
                    loadRoutineWithExercises(routineId)
                } else {
                    // Handle case where routine is not found
                    // You might want to add error handling or logging here
                }
            } catch (e: Exception) {
                // Handle any errors that occur during the update
                // You might want to add error handling or logging here
            }
        }
    }

    fun addExerciseToRoutine(routineId: Long, exercise: Exercise) {
        viewModelScope.launch {
            try {
                routineRepository.addExerciseToRoutine(routineId, exercise.id)
                // Refresh the selected routine to reflect the changes
                loadRoutineWithExercises(routineId)
            } catch (e: Exception) {
                // Handle any errors that occur during the addition
                // You might want to add error handling or logging here
            }
        }
    }

    fun removeExerciseFromRoutine(routineId: Long, exercise: Exercise) {
        viewModelScope.launch {
            try {
                routineRepository.removeExerciseFromRoutine(routineId, exercise.id)
                // Refresh the selected routine to reflect the changes
                loadRoutineWithExercises(routineId)
            } catch (e: Exception) {
                // Handle any errors that occur during the removal
                // You might want to add error handling or logging here
            }
        }
    }
}