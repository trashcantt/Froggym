package com.sabdev.froggym.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String,
    val type: ExerciseType,
    val sets: Int,
    val reps: Int
)
enum class ExerciseType {
    GYM, CALISTHENICS
}