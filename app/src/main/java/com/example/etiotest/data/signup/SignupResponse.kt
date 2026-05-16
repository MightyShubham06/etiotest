package com.example.etiotest.data.signup

import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("jobId")
    val jobId: String,
    
    @SerializedName("email")
    val email: String
)