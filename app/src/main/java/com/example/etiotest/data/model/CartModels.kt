package com.example.etiotest.data.model

import com.example.etiotest.data.LabTests

data class AddToCartRequest(
    val testId: String
)

data class CartViewResponse(
    val success: Boolean,
    val message: String,
    val data: List<LabTests>
)

data class AddToCartResponse(
    val success: Boolean,
    val message: String
)