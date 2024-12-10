package com.example.lab1.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity (
    @PrimaryKey val name: String,
    val height: Int?,
    val mass: Int?,
    val hairColor: String?,
    val eyeColor: String?,
    val gender: String?,
    val homeworld: String?
)
