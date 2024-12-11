package com.example.lab1.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity (
    @PrimaryKey val name: String,
    val height: Int?,
    val mass: Int?,
    val hair_color: String?,
    val eye_color: String?,
    val gender: String?,
    val homeworld: String?
)
