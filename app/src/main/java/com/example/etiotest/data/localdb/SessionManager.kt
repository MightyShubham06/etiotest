package com.example.etiotest.data.localdb

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_KEEP_ME_LOGGED_IN = "keep_me_logged_in"
    }

    /**
     * Saves the session.
     * @param keepMeLoggedIn If true, the session is treated as persistent for the bypass logic.
     */
    fun saveSession(
        accessToken: String,
        refreshToken: String,
        userId: String,
        name: String,
        keepMeLoggedIn: Boolean = true
    ) {
        prefs.edit().apply {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_KEEP_ME_LOGGED_IN, keepMeLoggedIn)
            apply()
        }
    }

    // Existing getters
    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    /**
     * Logic for Splash Bypass:
     * Checks if the user is logged in AND if they chose to stay logged in.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) &&
                prefs.getBoolean(KEY_KEEP_ME_LOGGED_IN, false) &&
                !getAccessToken().isNullOrEmpty()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}