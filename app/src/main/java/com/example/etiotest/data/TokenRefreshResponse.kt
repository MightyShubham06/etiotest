package com.example.etiotest.data

import com.google.gson.annotations.SerializedName

data class TokenRefreshResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)