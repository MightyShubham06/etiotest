package com.example.etiotest.data.request

data class PlaceOrderRequest(
    val patientId: String,
    val addressId: String,
    val testIds: List<String>,
    val bookingDate: String,
    val timeSlot: TimeSlot,
    val notes: String,
    val status: String = "pending",
    val paymentStatus: String = "pending"
)

data class TimeSlot(
    val startTime: String,
    val endTime: String,
    val slotLabel: String
)