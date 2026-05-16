package com.example.etiotest.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class OrderDetailResponse(
    val success: Boolean,
    val message: String,
    val data: OrderItem
)

@Parcelize
data class OrderItem(
    val _id: String,
    val orderNumber: String,
    val bookingDate: String,
    val status: String,
    val paymentStatus: String,
    val patient: Patient,
    val address: Address,
    val timeSlot: TimeSlot,
    val pricing: Pricing,
    val tests: List<TestDetails>
) : Parcelable

@Parcelize
data class TimeSlot(
    val startTime: String,
    val endTime: String,
    val slotLabel: String
) : Parcelable

@Parcelize
data class Patient(
    val name: String,
    val phone: String,
    val age: Int,
    val gender: String
) : Parcelable

@Parcelize
data class Address(
    val name: String,
    val line1: String,
    val city: String,
    val state: String,
    val pincode: String,
    val locality: String
): Parcelable

@Parcelize
data class Pricing(
    val subtotal: Int,
    val discount: Int,
    val total: Int
): Parcelable

@Parcelize
data class TestDetails(
    val name: String,
    val price: Int,
    val duration: String,
    val sampleType: String
): Parcelable