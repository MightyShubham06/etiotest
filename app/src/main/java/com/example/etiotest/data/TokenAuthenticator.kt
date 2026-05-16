package com.example.etiotest.data

import com.example.etiotest.data.localdb.SessionManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route


class TokenAuthenticator(
    private val sessionManager: SessionManager,
    private val apiService: ApiService // Pass the API service to make the refresh call
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 1. Check if we have a refresh token. If not, we can't refresh.
        val refreshToken = sessionManager.getRefreshToken() ?: return null

        synchronized(this) {
            // 2. Check if the token was already refreshed by another thread
            val currentToken = sessionManager.getAccessToken()
            val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")

            // If the token in the request is different from what's in session,
            // it means another thread already refreshed it. Just retry with new token.
            if (currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // 3. Make the Synchronous call to Refresh Token API
            // Note: We use .execute() instead of .enqueue() because this must be synchronous
            val refreshResponse = apiService.refreshAccessToken(refreshToken).execute()

            return if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                val newTokens = refreshResponse.body()!!

                // 4. Save the new tokens
                sessionManager.saveSession(
                    accessToken = newTokens.accessToken,
                    refreshToken = newTokens.refreshToken,
                    userId = sessionManager.getUserId() ?: "",
                    name = sessionManager.getUserName() ?: ""
                )

                // 5. Retry the original request with the NEW access token
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newTokens.accessToken}")
                    .build()
            } else {
                // Refresh failed (token expired or revoked) -> Clear session/Logout
                sessionManager.logout()
                null // Return null to stop retrying and let the 401 reach the UI
            }
        }
    }
}