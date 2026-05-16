package com.example.etiotest.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
data class TestListResponse(
    val tests: List<LabTests>,
    val pagination: Pagination
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val pages: Int
)

@Parcelize
data class LabTests(
    @SerializedName("_id") val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val originalPrice: Int,
    val discountPercentage: Int,
    val duration: String,
    val sampleType: String,
    val preparation: List<String>,
    val image: String,
    val displayPrice: Int
) : Parcelable