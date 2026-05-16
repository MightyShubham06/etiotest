package com.example.etiotest.data.model

data class MyOrdersResponse(
    val success: Boolean,
    val message: String,
    val data: List<OrderItem>
)