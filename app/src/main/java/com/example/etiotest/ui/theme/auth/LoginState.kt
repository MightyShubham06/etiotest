package com.example.etiotest.ui.theme.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.model.LoginResponse
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    // Returns the response containing jobId and email for the next step
    data class Success(val response: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>(LoginState.Idle)
    val loginState: LiveData<LoginState> get() = _loginState

    fun login(email: String) {
        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val response = authRepository.login(email)

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // Successfully received jobId and email
                        _loginState.value = LoginState.Success(body)
                    } else {
                        _loginState.value = LoginState.Error("Empty response from server")
                    }
                } else {
                    // Handle server errors (e.g., 401 Unauthorized)
                    val errorMsg = response.errorBody()?.string() ?: "Login failed: ${response.code()}"
                    _loginState.value = LoginState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }
}