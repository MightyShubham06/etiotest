package com.example.etiotest.data.request

data class InitiatePaymentRequest(
    val order_id: String,
    val amount: String,
    val customer_id: String,
    val customer_email: String,
    val customer_phone: String,
    val action: String = "paymentPage"
)