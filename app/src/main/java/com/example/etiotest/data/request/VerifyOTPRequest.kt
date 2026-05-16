package com.example.etiotest.data.request

import com.google.gson.annotations.SerializedName

data class VerifyOtpRequest(
    @SerializedName("jobId")
    val jobId: String,

    @SerializedName("otp")
    val otp: String
)
