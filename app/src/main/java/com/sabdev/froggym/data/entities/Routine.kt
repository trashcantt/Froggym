package com.sabdev.froggym.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class Routine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = ""
)