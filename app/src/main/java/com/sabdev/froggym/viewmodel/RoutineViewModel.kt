package com.sabdev.froggym.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.ExerciseType
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.data.repository.ExerciseRepository
import com.sabdev.froggym.data.repository.RoutineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    init {
        viewModelScope.launch {
            exerciseRepository.insertPredefinedExercises()
            loadRoutines()
            loadExercises()
            loadGymExercises()
            loadCalisthenicExercises()
        }
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

    fun createRoutine(name: String, type: ExerciseType, exerciseIds: List<Int>) {
        viewModelScope.launch {
            val newRoutine = Routine(name = name, type = type, exerciseIds = exerciseIds)
            routineRepository.insertRoutine(newRoutine)
        }
    }

    fun deleteRoutine(routine: Routine) {
        viewModelScope.launch {
            routineRepository.deleteRoutine(routine)
        }
    }

    fun getExercisesByType(type: ExerciseType): StateFlow<List<Exercise>> {
        return when (type) {
            ExerciseType.GYM -> gymExercises
            ExerciseType.CALISTHENICS -> calisthenicExercises
        }
    }
}