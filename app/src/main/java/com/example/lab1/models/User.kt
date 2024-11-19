package com.example.lab1.models

import java.io.Serializable

data class User (
    val username: String,
    val email: String,
    val password: String
) : Serializable

