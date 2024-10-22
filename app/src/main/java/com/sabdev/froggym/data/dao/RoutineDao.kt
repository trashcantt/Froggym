package com.sabdev.froggym.data.dao

import androidx.room.*
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.data.entities.ExerciseType
import com.sabdev.froggym.data.entities.RoutineExerciseCrossRef
import com.sabdev.froggym.data.entities.RoutineWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routines WHERE id = :id")
    fun getRoutineById(id: Int): Flow<Routine?>

    @Transaction
    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineWithExercises(id: Int): RoutineWithExercises

    @Query("SELECT * FROM routines WHERE type = :type")
    fun getRoutinesByType(type: ExerciseType): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE id = :id")
    fun getRoutineById(id: Long): Flow<Routine?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine): Long

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExerciseCrossRef(crossRef: RoutineExerciseCrossRef)

    @Transaction
    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineWithExercises(id: Long): RoutineWithExercises
}