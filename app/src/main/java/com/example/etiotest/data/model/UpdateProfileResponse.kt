package com.example.etiotest.data.model

data class UpdateProfileResponse(
    val _id: String,
    val name: String,
    val phone: String,
    val age: Int,
    val gender: String,
    val user: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)