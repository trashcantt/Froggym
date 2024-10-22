package com.sabdev.froggym.data.repository

import com.sabdev.froggym.data.dao.ExerciseDao
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.ExerciseType
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    val allExercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()

    fun getExercisesByType(type: ExerciseType): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByType(type)
    }

    suspend fun getExerciseById(id: Long): Exercise? {
        return exerciseDao.getExerciseById(id)
    }

suspend fun insertPredefinedExercises() {
    if (exerciseDao.getExerciseCount() == 0) {
        val exercises = listOf(
            // Ejercicios de Gimnasio
            Exercise(1L, "Press de banca", "Ejercicio de fuerza para el pecho", ExerciseType.GYM, reps = 10, sets = 3),
            Exercise(2L, "Sentadillas", "Ejercicio compuesto para piernas", ExerciseType.GYM, reps = 12, sets = 3),
            Exercise(3L, "Peso muerto", "Ejercicio compuesto para espalda y piernas", ExerciseType.GYM, reps = 8, sets = 3),
            Exercise(4L, "Press militar", "Ejercicio para hombros", ExerciseType.GYM, reps = 10, sets = 3),
            Exercise(5L, "Remo con barra", "Ejercicio para espalda", ExerciseType.GYM, reps = 12, sets = 3),
            Exercise(6L, "Curl de bíceps", "Ejercicio para bíceps", ExerciseType.GYM, reps = 12, sets = 3),
            Exercise(7L, "Extensiones de tríceps", "Ejercicio para tríceps", ExerciseType.GYM, reps = 12, sets = 3),
            Exercise(8L, "Zancadas", "Ejercicio para piernas y glúteos", ExerciseType.GYM, reps = 10, sets = 3),
            Exercise(9L, "Prensa de piernas", "Ejercicio para cuádriceps", ExerciseType.GYM, reps = 12, sets = 3),
            Exercise(10L, "Elevaciones laterales", "Ejercicio para deltoides", ExerciseType.GYM, reps = 15, sets = 3),
            Exercise(11L, "Cruce de poleas", "Ejercicio para pecho", ExerciseType.GYM, reps = 12, sets = 3),
            Exercise(12L, "Curl de martillo", "Ejercicio para bíceps y antebrazos", ExerciseType.GYM, reps = 12, sets = 3),
            Exercise(13L, "Extensiones de cuádriceps", "Ejercicio para cuádriceps", ExerciseType.GYM, reps = 15, sets = 3),
            Exercise(14L, "Curl femoral", "Ejercicio para isquiotibiales", ExerciseType.GYM, reps = 12, sets = 3),
            Exercise(15L, "Face pull", "Ejercicio para hombros y trapecios", ExerciseType.GYM, reps = 15, sets = 3),

            // Ejercicios de Calistenia
            Exercise(16L, "Flexiones", "Ejercicio para pecho y tríceps", ExerciseType.CALISTHENICS, reps = 15, sets = 3),
            Exercise(17L, "Dominadas", "Ejercicio para espalda y bíceps", ExerciseType.CALISTHENICS, reps = 8, sets = 3),
            Exercise(18L, "Dips", "Ejercicio para tríceps y pecho", ExerciseType.CALISTHENICS, reps = 12, sets = 3),
            Exercise(19L, "Sentadillas sin peso", "Ejercicio para piernas", ExerciseType.CALISTHENICS, reps = 20, sets = 3),
            Exercise(20L, "Burpees", "Ejercicio de cuerpo completo", ExerciseType.CALISTHENICS, reps = 15, sets = 3),
            Exercise(21L, "Plancha", "Ejercicio para core", ExerciseType.CALISTHENICS, reps = 60, sets = 3), // 60 segundos
            Exercise(22L, "Mountain climbers", "Ejercicio para core y cardio", ExerciseType.CALISTHENICS, reps = 30, sets = 3),
            Exercise(23L, "Pistol squats", "Ejercicio avanzado para piernas", ExerciseType.CALISTHENICS, reps = 5, sets = 3),
            Exercise(24L, "Muscle ups", "Ejercicio avanzado para pecho y espalda", ExerciseType.CALISTHENICS, reps = 5, sets = 3),
            Exercise(25L, "L-sit", "Ejercicio para core y brazos", ExerciseType.CALISTHENICS, reps = 30, sets = 3), // 30 segundos
            Exercise(26L, "Handstand push-ups", "Ejercicio avanzado para hombros", ExerciseType.CALISTHENICS, reps = 5, sets = 3), Exercise(27L, "Dragon flag", "Ejercicio avanzado para core", ExerciseType.CALISTHENICS, reps = 5, sets = 3),
            Exercise(28L, "Human flag", "Ejercicio avanzado de fuerza", ExerciseType.CALISTHENICS, reps = 10, sets = 3), // 10 segundos de mantenimiento
            Exercise(29L, "Front lever", "Ejercicio avanzado para espalda y core", ExerciseType.CALISTHENICS, reps = 10, sets = 3), // 10 segundos de mantenimiento
            Exercise(30L, "Back lever", "Ejercicio avanzado para espalda y core", ExerciseType.CALISTHENICS, reps = 10, sets = 3) // 10 segundos de mantenimiento
        )
        exerciseDao.insertAll(exercises)
        }
    }
}