package com.example.etiotest.data.model

data class UpdateProfileRequest(
    val name: String,
    val age: Int
)

data class CommonResponse(
    val message: String
)