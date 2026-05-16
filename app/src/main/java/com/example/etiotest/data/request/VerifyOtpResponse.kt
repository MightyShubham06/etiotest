package com.example.etiotest.data.request

import com.google.gson.annotations.SerializedName

data class VerifyOtpResponse(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("refreshToken")
    val refreshToken: String,

    @SerializedName("user")
    val user: UserData
)

data class UserData(
    @SerializedName("id")
    val id: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("userType")
    val userType: String
)
