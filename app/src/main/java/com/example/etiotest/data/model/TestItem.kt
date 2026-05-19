package com.example.etiotest.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TestItem(
    val name: String,
    val price: Int
) : Parcelable