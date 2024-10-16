package com.sabdev.froggym.data.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RoutineWithExercises(
    @Embedded val routine: Routine,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(RoutineExerciseCrossRef::class)
    )
    val exercises: List<Exercise>
)