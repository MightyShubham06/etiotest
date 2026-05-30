package com.example.etiotest.data.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.etiotest.data.AuthRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class SignupViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _signupState =
        MutableLiveData<SignupState>(SignupState.Idle)

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

                val response: Response<SignupResponse> =
                    authRepository.signup(
                        email,
                        phone,
                        name,
                        userType
                    )

                if (response.isSuccessful &&
                    response.body() != null
                ) {

                    _signupState.value =
                        SignupState.Success(response.body()!!)

                } else {

                    val errorBody =
                        response.errorBody()?.string()

                    val message = try {

                        JSONObject(errorBody ?: "")
                            .getString("message")

                    } catch (e: Exception) {

                        "Something went wrong"
                    }

                    _signupState.value =
                        SignupState.Error(message)
                }

            } catch (e: IOException) {

                _signupState.value =
                    SignupState.Error(
                        "No internet connection"
                    )

            } catch (e: Exception) {

                _signupState.value =
                    SignupState.Error(
                        e.localizedMessage
                            ?: "Unexpected error"
                    )
            }
        }
    }

    fun resetState() {
        _signupState.value = SignupState.Idle
    }
}