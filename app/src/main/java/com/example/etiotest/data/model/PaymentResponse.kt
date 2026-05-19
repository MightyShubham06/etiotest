package com.example.etiotest.data.model

data class PaymentResponse(
    val sdkPayload: SdkPayload
)

data class SdkPayload(
    val requestId: String,
    val service: String,
    val payload: PayloadData
)

data class PayloadData(
    val clientId: String,
    val environment: String,
    val orderDetails: String
)