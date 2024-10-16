package com.sabdev.froggym.data.repository

import com.sabdev.froggym.data.dao.RoutineDao
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.data.entities.ExerciseType
import kotlinx.coroutines.flow.Flow

class RoutineRepository(private val routineDao: RoutineDao) {

    fun getRoutinesByType(type: ExerciseType): Flow<List<Routine>> {
        return routineDao.getRoutinesByType(type)
    }

    suspend fun insertRoutine(routine: Routine) {
        routineDao.insertRoutine(routine)
    }

    suspend fun deleteRoutine(routine: Routine) {
        routineDao.deleteRoutine(routine)
    }
}