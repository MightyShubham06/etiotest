package com.example.etiotest.data.model

import com.google.gson.annotations.SerializedName

data class AddressRequest(
    @SerializedName("name") 
    val name: String,
    
    @SerializedName("phone") 
    val phone: String,
    
    @SerializedName("addressType") 
    val addressType: String, // e.g., "home", "work"
    
    @SerializedName("line1") 
    val line1: String,
    
    @SerializedName("line2") 
    val line2: String?, // Optional field
    
    @SerializedName("locality") 
    val locality: String,
    
    @SerializedName("landmark") 
    val landmark: String?,
    
    @SerializedName("city") 
    val city: String,
    
    @SerializedName("district") 
    val district: String,
    
    @SerializedName("state") 
    val state: String,
    
    @SerializedName("pincode") 
    val pincode: String,
    
    @SerializedName("alternatePhone") 
    val alternatePhone: String?
)