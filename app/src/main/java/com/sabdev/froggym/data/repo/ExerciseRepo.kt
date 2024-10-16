package com.sabdev.froggym.data.repo

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
                // Ejercicios para Pecho (Gimnasio)
                Exercise(1, "Press de banca", "Ejercicio de fuerza para el pecho", ExerciseType.GYM),
                Exercise(2, "Aperturas con mancuernas", "Ejercicio de aislamiento para el pecho", ExerciseType.GYM),
                Exercise(3, "Press inclinado", "Ejercicio de fuerza para la parte superior del pecho", ExerciseType.GYM),
                // Ejercicios para Pecho (Calistenia)
                Exercise(4, "Flexiones", "Ejercicio de empuje para el pecho y tríceps", ExerciseType.CALISTHENICS),
                Exercise(5, "Fondos", "Ejercicio para pecho, tríceps y hombros", ExerciseType.CALISTHENICS),
                Exercise(6, "Flexiones en anillas", "Ejercicio avanzado para pecho y estabilidad", ExerciseType.CALISTHENICS),
                // Ejercicios para Piernas (Gimnasio)
                Exercise(7, "Sentadillas", "Ejercicio compuesto para piernas y glúteos", ExerciseType.GYM),
                Exercise(8, "Prensa de piernas", "Ejercicio de máquina para cuádriceps y glúteos", ExerciseType.GYM),
                Exercise(9, "Peso muerto", "Ejercicio compuesto para espalda baja y piernas", ExerciseType.GYM),
                // Ejercicios para Piernas (Calistenia)
                Exercise(10, "Sentadillas a una pierna", "Ejercicio avanzado de equilibrio y fuerza", ExerciseType.CALISTHENICS),
                Exercise(11, "Zancadas", "Ejercicio para piernas y glúteos", ExerciseType.CALISTHENICS),
                Exercise(12, "Sentadillas búlgaras", "Ejercicio unilateral para piernas", ExerciseType.CALISTHENICS),
                // Ejercicios para Espalda (Gimnasio)
                Exercise(13, "Remo con barra", "Ejercicio de fuerza para la espalda", ExerciseType.GYM),
                Exercise(14, "Pullover con mancuerna", "Ejercicio para la parte superior de la espalda", ExerciseType.GYM),
                Exercise(15, "Jalón al pecho", "Ejercicio de tracción para la espalda", ExerciseType.GYM),
                // Ejercicios para Espalda (Calistenia)
                Exercise(16, "Dominadas", "Ejercicio de tracción para espalda y bíceps", ExerciseType.CALISTHENICS),
                Exercise(17, "Remo invertido", "Ejercicio para la espalda media", ExerciseType.CALISTHENICS),
                Exercise(18, "Dominadas asistidas", "Variación más accesible de las dominadas", ExerciseType.CALISTHENICS),
                // Ejercicios para Core (Gimnasio)
                Exercise(19, "Elevaciones de piernas", "Ejercicio para el abdomen inferior", ExerciseType.GYM),
                Exercise(20, "Crunches", "Ejercicio clásico para abdominales", ExerciseType.GYM),
                Exercise(21, "Giros rusos", "Ejercicio para oblicuos", ExerciseType.GYM),
                // Ejercicios para Core (Calistenia)
                Exercise(22, "Plancha", "Ejercicio isométrico para el core", ExerciseType.CALISTHENICS),
                Exercise(23, "Escaladores", "Ejercicio dinámico para abdominales y cardio", ExerciseType.CALISTHENICS),
                Exercise(24, "Plancha lateral", "Ejercicio para oblicuos y estabilidad", ExerciseType.CALISTHENICS),
                // Ejercicios para Cuerpo Completo (Gimnasio)
                Exercise(25, "Cargada y envión", "Ejercicio olímpico de cuerpo completo", ExerciseType.GYM),
                Exercise(26, "Arrancada", "Ejercicio olímpico de potencia", ExerciseType.GYM),
                Exercise(27, "Thruster", "Ejercicio compuesto de press y sentadilla", ExerciseType.GYM),
                // Ejercicios para Cuerpo Completo (Calistenia)
                Exercise(28, "Burpees", "Ejercicio de alto impacto para todo el cuerpo", ExerciseType.CALISTHENICS),
                Exercise(29, "Muscle ups", "Ejercicio avanzado de tracción y empuje", ExerciseType.CALISTHENICS),
                Exercise(30, "Sentadillas a una pierna", "Ejercicio de fuerza y equilibrio", ExerciseType.CALISTHENICS)
            )
            exerciseDao.insertAll(exercises)
        }
    }

    suspend fun deleteAllExercises() {
        exerciseDao.deleteAllExercises()
    }
}