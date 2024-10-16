package com.sabdev.froggym.data.dao

import androidx.room.*
import com.sabdev.froggym.data.entities.Exercise
import com.sabdev.froggym.data.entities.ExerciseType
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE type = :type")
    fun getExercisesByType(type: ExerciseType): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Int): Exercise?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getExerciseCount(): Int
}