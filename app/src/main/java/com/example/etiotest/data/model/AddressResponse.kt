package com.example.etiotest.data.model

import com.google.gson.annotations.SerializedName

data class AddressResponse(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("addressType") val addressType: String,
    @SerializedName("line1") val line1: String,
    @SerializedName("line2") val line2: String?,
    @SerializedName("locality") val locality: String,
    @SerializedName("landmark") val landmark: String?,
    @SerializedName("city") val city: String,
    @SerializedName("district") val district: String,
    @SerializedName("state") val state: String,
    @SerializedName("pincode") val pincode: String,
    @SerializedName("alternatePhone") val alternatePhone: String?,
    @SerializedName("isDefault") val isDefault: Boolean,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("user") val userId: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)