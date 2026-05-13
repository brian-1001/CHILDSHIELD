package com.example.childshield.models

data class ChildModel(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val hairColor: String = "",
    val distinguishingFeatures: String = "",
    val dateMissing: String = "",
    val timeMissing: String = "",
    val lastSeenLocation: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val description: String = "",
    val emergencyContact: String = "",
    val imageUrl: String = "",
    val status: String = "Missing", // Missing or Found
    val reporterId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
