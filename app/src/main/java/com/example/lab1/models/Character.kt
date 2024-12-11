package com.example.lab1.models

@kotlinx.serialization.Serializable
data class Character(
    val name: String? = null,
    val height: Int? = null,
    val mass: Int? = null,
    val hair_color: String? = null,
    val eye_color: String? = null,
    val gender: String? = null,
    val homeworld: String?
)
