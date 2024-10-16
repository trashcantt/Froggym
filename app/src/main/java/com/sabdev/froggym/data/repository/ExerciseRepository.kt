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

    suspend fun getExerciseById(id: Int): Exercise? {
        return exerciseDao.getExerciseById(id)
    }

    suspend fun insertPredefinedExercises() {
        if (exerciseDao.getExerciseCount() == 0) {
            val exercises = listOf(
                // Ejercicios de Gimnasio
                Exercise(1, "Press de banca", "Ejercicio de fuerza para el pecho", ExerciseType.GYM),
                Exercise(2, "Sentadillas", "Ejercicio compuesto para piernas", ExerciseType.GYM),
                Exercise(3, "Peso muerto", "Ejercicio compuesto para espalda y piernas", ExerciseType.GYM),
                Exercise(4, "Press militar", "Ejercicio para hombros", ExerciseType.GYM),
                Exercise(5, "Remo con barra", "Ejercicio para espalda", ExerciseType.GYM),
                Exercise(6, "Curl de bíceps", "Ejercicio para bíceps", ExerciseType.GYM),
                Exercise(7, "Extensiones de tríceps", "Ejercicio para tríceps", ExerciseType.GYM),
                Exercise(8, "Zancadas", "Ejercicio para piernas y glúteos", ExerciseType.GYM),
                Exercise(9, "Prensa de piernas", "Ejercicio para cuádriceps", ExerciseType.GYM),
                Exercise(10, "Elevaciones laterales", "Ejercicio para deltoides", ExerciseType.GYM),
                Exercise(11, "Cruce de poleas", "Ejercicio para pecho", ExerciseType.GYM),
                Exercise(12, "Curl de martillo", "Ejercicio para bíceps y antebrazos", ExerciseType.GYM),
                Exercise(13, "Extensiones de cuádriceps", "Ejercicio para cuádriceps", ExerciseType.GYM),
                Exercise(14, "Curl femoral", "Ejercicio para isquiotibiales", ExerciseType.GYM),
                Exercise(15, "Face pull", "Ejercicio para hombros y trapecios", ExerciseType.GYM),

                // Ejercicios de Calistenia
                Exercise(16, "Flexiones", "Ejercicio para pecho y tríceps", ExerciseType.CALISTHENICS),
                Exercise(17, "Dominadas", "Ejercicio para espalda y bíceps", ExerciseType.CALISTHENICS),
                Exercise(18, "Dips", "Ejercicio para tríceps y pecho", ExerciseType.CALISTHENICS),
                Exercise(19, "Sentadillas sin peso", "Ejercicio para piernas", ExerciseType.CALISTHENICS),
                Exercise(20, "Burpees", "Ejercicio de cuerpo completo", ExerciseType.CALISTHENICS),
                Exercise(21, "Plancha", "Ejercicio para core", ExerciseType.CALISTHENICS),
                Exercise(22, "Mountain climbers", "Ejercicio para core y cardio", ExerciseType.CALISTHENICS),
                Exercise(23, "Pistol squats", "Ejercicio avanzado para piernas", ExerciseType.CALISTHENICS),
                Exercise(24, "Muscle ups", "Ejercicio avanzado para pecho y espalda", ExerciseType.CALISTHENICS),
                Exercise(25, "L-sit", "Ejercicio para core y brazos", ExerciseType.CALISTHENICS),
                Exercise(26, "Handstand push-ups", "Ejercicio avanzado para hombros", ExerciseType.CALISTHENICS),
                Exercise(27, "Dragon flag", "Ejercicio avanzado para core", ExerciseType.CALISTHENICS),
                Exercise(28, "Human flag", "Ejercicio avanzado de fuerza", ExerciseType.CALISTHENICS),
                Exercise(29, "Front lever", "Ejercicio avanzado para espalda y core", ExerciseType.CALISTHENICS),
                Exercise(30, "Back lever", "Ejercicio avanzado para espalda y core", ExerciseType.CALISTHENICS)
            )
            exerciseDao.insertAll(exercises)
        }
    }
}