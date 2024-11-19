package com.example.lab1.models


@kotlinx.serialization.Serializable
data class PeopleResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Character>
)
