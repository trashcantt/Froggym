package com.sabdev.froggym.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val username: String,
    val name: String,
    val height: Float,
    val weight: Float,
    val profilePicturePath: String
)