package com.sabdev.froggym.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "type") val type: ExerciseType,
    @ColumnInfo(name = "exercise_ids") val exerciseIds: List<Int>
)