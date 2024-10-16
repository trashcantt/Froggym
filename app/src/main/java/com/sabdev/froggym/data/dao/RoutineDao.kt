package com.sabdev.froggym.data.dao

import androidx.room.*
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.data.entities.ExerciseType
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines WHERE type = :type")
    fun getRoutinesByType(type: ExerciseType): Flow<List<Routine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine)

    @Query("SELECT * FROM routines WHERE id = :id")
    fun getRoutineById(id: Int): Flow<Routine?>

    @Delete
    suspend fun deleteRoutine(routine: Routine)
}