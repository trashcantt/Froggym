package com.sabdev.froggym.data.entities

import androidx.room.Entity

@Entity(primaryKeys = ["routineId", "exerciseId"])
data class RoutineExerciseCrossRef(
    val routineId: Long,
    val exerciseId: Long
)