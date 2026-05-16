package com.example.etiotest.data.signup


sealed class SignupState {
    object Idle : SignupState()
    object Loading : SignupState()
    // Update this line:
    data class Success(val data: SignupResponse) : SignupState()
    data class Error(val message: String) : SignupState()
}