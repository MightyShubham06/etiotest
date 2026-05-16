package com.example.etiotest.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PatientItem(
    @SerializedName("_id") val id: String,
    val name: String,
    val phone: String,
    val age: Int,
    val gender: String,
    val isActive: Boolean
) : Parcelable