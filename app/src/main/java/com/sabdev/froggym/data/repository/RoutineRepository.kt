package com.sabdev.froggym.data.repository

import com.sabdev.froggym.data.dao.RoutineDao
import com.sabdev.froggym.data.entities.*
import kotlinx.coroutines.flow.*

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

    suspend fun updateRoutine(routine: Routine) {
        routineDao.updateRoutine(routine)
    }

    suspend fun removeExerciseFromRoutine(routineId: Long, exerciseId: Long) {
        routineDao.deleteRoutineExerciseCrossRef(RoutineExerciseCrossRef(routineId, exerciseId))
    }

    suspend fun initializePredefinedRoutines() {
        // Verificar si ya existen rutinas predefinidas
        if (routineDao.getRoutinesByType(ExerciseType.GYM).first().isEmpty() &&
            routineDao.getRoutinesByType(ExerciseType.CALISTHENICS).first().isEmpty()) {

            // Rutinas de gimnasio
            val pplPush = insertRoutineWithExercises("PPL Empuje", ExerciseType.GYM, listOf(1L, 4L, 7L, 11L))
            val pplPull = insertRoutineWithExercises("PPL Tirón", ExerciseType.GYM, listOf(3L, 5L, 6L, 15L))
            val pplLegs = insertRoutineWithExercises("PPL Pierna", ExerciseType.GYM, listOf(2L, 8L, 9L, 13L, 14L))

            val arnoldChestBack = insertRoutineWithExercises("Arnold Split Pecho & Espalda", ExerciseType.GYM, listOf(1L, 5L, 11L, 3L))
            val arnoldShoulderArms = insertRoutineWithExercises("Arnold Split Hombro & Armas", ExerciseType.GYM, listOf(4L, 10L, 6L, 7L, 12L))
            val arnoldLegs = insertRoutineWithExercises("Arnold Split Pierna", ExerciseType.GYM, listOf(2L, 9L, 13L, 14L))

            val heavyDutyUpper = insertRoutineWithExercises("Heavy Duty Tren Superior", ExerciseType.GYM, listOf(1L, 4L, 5L, 6L, 7L))
            val heavyDutyLower = insertRoutineWithExercises("Heavy Duty Tren Inferior", ExerciseType.GYM, listOf(2L, 3L, 9L, 13L, 14L))

            // Rutinas de calistenia
            val calistheniaBeginner = insertRoutineWithExercises("Calistenia Principiante", ExerciseType.CALISTHENICS, listOf(16L, 19L, 21L, 22L))
            val calistheniaIntermediate = insertRoutineWithExercises("Calistenia Intermedio", ExerciseType.CALISTHENICS, listOf(16L, 17L, 18L, 19L, 20L, 21L))
            val calistheniaAdvanced = insertRoutineWithExercises("Calistenia Avanzado", ExerciseType.CALISTHENICS, listOf(23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L))
        }
    }

    private suspend fun insertRoutineWithExercises(name: String, type: ExerciseType, exerciseIds: List<Long>): Long {
        val routineId = insertRoutine(Routine(name = name, type = type))
        exerciseIds.forEach { exerciseId ->
            addExerciseToRoutine(routineId, exerciseId)
        }
        return routineId
    }

}
