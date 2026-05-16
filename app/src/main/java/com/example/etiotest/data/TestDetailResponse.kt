package com.example.etiotest.data

import com.google.gson.annotations.SerializedName

data class TestDetailResponse(
    val status: String,
    val data: LabTest // This matches your JSON structure
)
