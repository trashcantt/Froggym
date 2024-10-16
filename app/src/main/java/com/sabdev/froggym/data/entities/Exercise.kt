package com.sabdev.froggym.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val type: ExerciseType
)

enum class ExerciseType {
    CALISTHENICS, GYM
}