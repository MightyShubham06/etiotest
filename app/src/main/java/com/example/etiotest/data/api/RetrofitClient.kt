// data/api/RetrofitClient.kt
package com.example.etiotest.data.api

import android.content.Context
import com.example.etiotest.data.ApiService
import com.example.etiotest.data.localdb.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://13.201.116.158:4000/api/"
    var apiService: ApiService? = null

    fun getApiService(context: Context): ApiService {
        return apiService ?: synchronized(this) {
            val sessionManager = SessionManager(context)

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .addInterceptor(AuthInterceptor(sessionManager)) // Auto-adds Token
                // .authenticator(TokenAuthenticator(context, sessionManager)) // Optional: Add this when your Refresh API is ready
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(ApiService::class.java)
            apiService = service
            service
        }
    }
}