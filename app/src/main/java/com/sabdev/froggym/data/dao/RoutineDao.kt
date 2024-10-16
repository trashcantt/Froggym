package com.sabdev.froggym.data.dao

import androidx.room.*
import com.sabdev.froggym.data.entities.Routine
import com.sabdev.froggym.data.entities.RoutineExerciseCrossRef
import com.sabdev.froggym.data.entities.RoutineWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: Routine): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExerciseCrossRef(crossRef: RoutineExerciseCrossRef)

    @Transaction
    @Query("SELECT * FROM routines")
    fun getRoutinesWithExercises(): Flow<List<RoutineWithExercises>>

    @Transaction
    @Query("SELECT * FROM routines WHERE id = :routineId")
    fun getRoutineWithExercises(routineId: Int): Flow<RoutineWithExercises>

    @Query("SELECT * FROM routines")
    fun getAllRoutines(): Flow<List<Routine>>

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Query("DELETE FROM RoutineExerciseCrossRef WHERE routineId = :routineId")
    suspend fun deleteRoutineExerciseCrossRefs(routineId: Int)

    @Transaction
    suspend fun deleteRoutineWithExercises(routine: Routine) {
        deleteRoutineExerciseCrossRefs(routine.id)
        deleteRoutine(routine)
    }
}