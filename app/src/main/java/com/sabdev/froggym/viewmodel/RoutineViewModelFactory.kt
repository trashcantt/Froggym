package com.sabdev.froggym.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sabdev.froggym.data.repository.RoutineRepository
import com.sabdev.froggym.data.repository.ExerciseRepository

class RoutineViewModelFactory(
    private val routineRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoutineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoutineViewModel(routineRepository, exerciseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}