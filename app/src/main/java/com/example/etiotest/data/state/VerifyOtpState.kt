package com.example.etiotest.data.state

import com.example.etiotest.data.request.VerifyOtpResponse

sealed class VerifyOtpState {
    object Idle : VerifyOtpState()
    object Loading : VerifyOtpState()
    data class Success(val data: VerifyOtpResponse) : VerifyOtpState()
    data class Error(val message: String) : VerifyOtpState()
}