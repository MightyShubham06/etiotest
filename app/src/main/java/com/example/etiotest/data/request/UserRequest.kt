package com.example.etiotest.data.request

data class UserRequest(
    val name: String,
    val phone: String,
    val age: Int,
    val gender: String
)
data class UserResponse(
    val _id: String,
    val name: String,
    val phone: String,
    val age: Int,
    val gender: String,
    val user: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)