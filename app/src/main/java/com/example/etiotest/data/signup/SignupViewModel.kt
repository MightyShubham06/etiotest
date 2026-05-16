package com.example.etiotest.data.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.etiotest.data.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class SignupViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

        private val _signupState = MutableLiveData<SignupState>(SignupState.Idle)
    val signupState: LiveData<SignupState> = _signupState

    fun signup(
        email: String,
        phone: String,
        name: String,
        userType: String
    ) {
        _signupState.value = SignupState.Loading

        viewModelScope.launch {
            try {
                // Call your repository’s signup API method
                val response: Response<SignupResponse> = authRepository.signup(email, phone, name, userType)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // Pass the body (which contains jobId) to the Success state
                        _signupState.value = SignupState.Success(body)
                    } else {
                        _signupState.value = SignupState.Error("Response body is empty")
                    }
                }
            } catch (e: Exception) {
                _signupState.value = SignupState.Error(e.localizedMessage ?: "Unexpected error")
            }
        }
    }

    // Optional: you can add reset or clear functions
    fun resetState() {
        _signupState.value = SignupState.Idle
    }
}
