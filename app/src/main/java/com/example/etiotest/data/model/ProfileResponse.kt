package com.example.etiotest.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileResponse(
    val _id: String,
    val email: String,
    val name: String,
    val phone: String,
    val userType: String
) : Parcelable