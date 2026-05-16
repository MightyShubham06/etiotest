package com.example.etiotest.data.model

data class UserProfileResponse(
    val _id: String,
    val email: String,
    val name: String,
    val phone: String,
    val userType: String,
    val createdAt: String,
    val updatedAt: String,
    val dateOfBirth: String,
    val gender: String,
    val profilePhoto: String
)