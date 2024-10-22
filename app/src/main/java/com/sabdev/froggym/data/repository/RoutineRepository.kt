package com.sabdev.froggym.data.repository

import com.sabdev.froggym.data.dao.RoutineDao
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.data.entities.ExerciseType
import com.sabdev.froggym.data.entities.RoutineExerciseCrossRef
import com.sabdev.froggym.data.entities.RoutineWithExercises
import kotlinx.coroutines.flow.Flow

class RoutineRepository(private val routineDao: RoutineDao) {

    fun getRoutinesByType(type: ExerciseType): Flow<List<Routine>> {
        return routineDao.getRoutinesByType(type)
    }

    fun getRoutineById(id: Long): Flow<Routine?> {
        return routineDao.getRoutineById(id)
    }

    suspend fun insertRoutine(routine: Routine): Long {
        return routineDao.insertRoutine(routine)
    }

    suspend fun deleteRoutine(routine: Routine) {
        routineDao.deleteRoutine(routine)
    }

    suspend fun addExerciseToRoutine(routineId: Long, exerciseId: Long) {
        routineDao.insertRoutineExerciseCrossRef(RoutineExerciseCrossRef(routineId, exerciseId))
    }

    suspend fun getRoutineWithExercisesById(id: Long): RoutineWithExercises {
        return routineDao.getRoutineWithExercises(id)
    }
}
