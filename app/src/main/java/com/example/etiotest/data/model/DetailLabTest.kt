@file:Suppress("DEPRECATED_ANNOTATION")

package com.example.etiotest.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailLabTest(
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("price")
    val price: Int,
    
    @SerializedName("originalPrice")
    val originalPrice: Int,
    
    @SerializedName("discountPercentage")
    val discountPercentage: Int,
    
    @SerializedName("duration")
    val duration: String,
    
    @SerializedName("sampleType")
    val sampleType: String,
    
    @SerializedName("preparation")
    val preparation: List<String>,
    
    @SerializedName("image")
    val image: String,
    
    @SerializedName("isActive")
    val isActive: Boolean,
    
    @SerializedName("displayPrice")
    val displayPrice: Int,
    
    @SerializedName("createdAt")
    val createdAt: String,
    
    @SerializedName("updatedAt")
    val updatedAt: String,
    
    @SerializedName("__v")
    val version: Int
) : Parcelable