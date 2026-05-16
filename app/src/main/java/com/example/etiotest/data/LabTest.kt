package com.example.etiotest.data

data class LabTest(
    val id: String,
    val name: String,
    val description: String,
    val originalPrice: Double,
    val discountedPrice: Double,
    val imageUrl: String // URL from API
)
