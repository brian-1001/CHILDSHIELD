package com.example.childshield.models

data class ChildModel(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val description: String = "",
    val lastSeenLocation: String = "",
    val imageUrl: String = "",
    val status: String = "Missing", // Missing or Found
    val reporterId: String = ""
)
