// ui/splash/SplashViewModel.kt
package com.example.etiotest.ui.theme.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Splash screen: waits a bit and emits event whether to navigate to login or main
 */
class SplashViewModel : ViewModel() {

    private val _navigateTo = MutableLiveData<SplashNavTarget>()
    val navigateTo: LiveData<SplashNavTarget> get() = _navigateTo

    init {
        viewModelScope.launch {
            delay(2000) // simulate loading/splash
            // check login status from stored token, prefs etc
            val loggedIn = false // replace with actual check
            if (loggedIn) {
                _navigateTo.value = SplashNavTarget.MAIN
            } else {
                _navigateTo.value = SplashNavTarget.LOGIN
            }
        }
    }
}

enum class SplashNavTarget {
    LOGIN,
    MAIN
}
