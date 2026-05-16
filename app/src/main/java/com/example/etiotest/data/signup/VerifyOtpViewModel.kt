package com.example.etiotest.data.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.request.VerifyOtpRequest
import com.example.etiotest.data.request.VerifyOtpResponse
import com.example.etiotest.data.state.VerifyOtpState
import kotlinx.coroutines.launch
import retrofit2.Response

class VerifyOtpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Use the new VerifyOtpState instead of SignupState
    private val _verifyState = MutableLiveData<VerifyOtpState>(VerifyOtpState.Idle)
    val verifyState: LiveData<VerifyOtpState> = _verifyState

    fun verifyOtp(jobId: String, otp: String?) {
        if (otp == null || otp.length < 6) {
            _verifyState.value = VerifyOtpState.Error("Please enter a 6-digit OTP")
            return
        }

        _verifyState.value = VerifyOtpState.Loading

        viewModelScope.launch {
            try {
                // Call repository: Ensure your repository returns Response<VerifyOtpResponse>
                val response: Response<VerifyOtpResponse> = authRepository.verifyOTP(otp, jobId)

                if (response.isSuccessful && response.body() != null) {
                    // Success! Pass the tokens and user data back to the Fragment
                    _verifyState.value = VerifyOtpState.Success(response.body()!!)
                } else {
                    val errMsg = response.errorBody()?.string() ?: "Invalid OTP"
                    _verifyState.value = VerifyOtpState.Error(errMsg)
                }
            } catch (e: Exception) {
                _verifyState.value = VerifyOtpState.Error(e.localizedMessage ?: "Connection failed")
            }
        }
    }

    fun resetState() {
        _verifyState.value = VerifyOtpState.Idle
    }
}