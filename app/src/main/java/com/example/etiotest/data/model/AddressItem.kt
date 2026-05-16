package com.example.etiotest.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressItem(
    @SerializedName("_id")
    val id: String?,
    val name: String,
    val phone: String,
    val line1: String?,
    val line2: String?,
    val locality: String?,
    val city: String?,
    val state: String?,
    val pincode: String?,
    val landmark: String?,
    val isDefault: Boolean
) : Parcelable