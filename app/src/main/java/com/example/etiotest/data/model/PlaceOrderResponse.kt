package com.example.etiotest.data.model

data class PlaceOrderResponse(
    val success: Boolean,
    val message: String,
    val data: OrderData
)

data class OrderData(
    val _id: String,
    val orderNumber: String,
    val bookingDate: String,
    val status: String
)