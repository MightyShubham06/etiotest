// data/api/ApiService.kt
package com.example.etiotest.data

import com.example.etiotest.data.request.VerifyOtpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

    data class LoginRequest(val email: String)
data class UserProfile(val id: String, val name: String, val email: String)
data class SignupRequest(
    val email: String,
    val phone: String,
    val name: String,
    val userType: String
)




