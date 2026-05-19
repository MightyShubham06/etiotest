package com.example.etiotest.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.model.AddressItem
import com.example.etiotest.data.model.AddressRequest
import com.example.etiotest.data.model.AddressResponse
import com.example.etiotest.data.model.OrderItem
import com.example.etiotest.data.model.PatientItem
import com.example.etiotest.data.model.PaymentResponse
import com.example.etiotest.data.model.PlaceOrderResponse
import com.example.etiotest.data.model.UpdateProfileRequest
import com.example.etiotest.data.model.UpdateProfileResponse
import com.example.etiotest.data.request.InitiatePaymentRequest
import com.example.etiotest.data.request.PlaceOrderRequest
import com.example.etiotest.data.request.UserRequest
import com.example.etiotest.data.request.UserResponse
import kotlinx.coroutines.launch

class UserViewModel(private val repository: AuthRepository) : ViewModel() {

    // Patient Save Result
    private val _saveResult = MutableLiveData<Result<UserResponse>>()
    val saveResult: LiveData<Result<UserResponse>> = _saveResult

    // Address Save Result (Mapped to AddressResponse to capture the new ID)
    private val _addressSaveResult = MutableLiveData<Result<AddressResponse>>()
    val addressSaveResult: LiveData<Result<AddressResponse>> = _addressSaveResult

    private val _orderDetail = MutableLiveData<Resource<OrderItem>>()
    val orderDetail: LiveData<Resource<OrderItem>> = _orderDetail

    /**
     * Saves patient details to the registry
     */
    fun saveUserDetails(name: String, phone: String, age: String, gender: String) {
        viewModelScope.launch {
            try {
                val request = UserRequest(name, phone, age.toInt(), gender)
                val response = repository.createUser(request)

                if (response.isSuccessful && response.body() != null) {
                    _saveResult.postValue(Result.success(response.body()!!))
                } else {
                    _saveResult.postValue(Result.failure(Exception(response.message())))
                }
            } catch (e: Exception) {
                _saveResult.postValue(Result.failure(e))
            }
        }
    }
    /**
     * Executes the API call for adding a new address
     */
    fun saveAddress(address: AddressRequest) {
        viewModelScope.launch {
            try {
                // Ensure repository.addAddress returns Response<AddressResponse>
                val response = repository.addAddress(address)

                if (response.isSuccessful && response.body() != null) {
                    _addressSaveResult.postValue(Result.success(response.body()!!))
                } else {
                    _addressSaveResult.postValue(Result.failure(Exception("Failed to save: ${response.code()}")))
                }
            } catch (e: Exception) {
                _addressSaveResult.postValue(Result.failure(e))
            }
        }
    }

    private val _addressList = MutableLiveData<Resource<List<AddressItem>>>()
    val addressList: LiveData<Resource<List<AddressItem>>> = _addressList

    /**
     * Factory for Dependency Injection
     */
    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    /**
     * Order Details
     */
    fun getOrderDetailsById(orderId: String) {
        viewModelScope.launch {
            _orderDetail.value = Resource.Loading()
            try {
                val response = repository.getOrderById(orderId)
                if (response.isSuccessful && response.body() != null) {
                    _orderDetail.value = Resource.Success(response.body()!!.data)
                } else {
                    _orderDetail.value = Resource.Error("Server Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _orderDetail.value = Resource.Error(e.message ?: "Network error occurred")
            }
        }
    }

    private val _updateProfileResponse = MutableLiveData<Resource<UpdateProfileResponse>>()
    val updateProfileResponse: LiveData<Resource<UpdateProfileResponse>> = _updateProfileResponse

    fun updateProfile(id: String, request: UpdateProfileRequest) {
        viewModelScope.launch {
            _updateProfileResponse.value = Resource.Loading()

            try {
                val res = repository.updateProfile(id, request)

                if (res.isSuccessful && res.body() != null) {
                    _updateProfileResponse.value = Resource.Success(res.body()!!)
                } else {
                    _updateProfileResponse.value = Resource.Error(res.message())
                }

            } catch (e: Exception) {
                _updateProfileResponse.value =
                    Resource.Error(e.message ?: "Something went wrong")
            }
        }
    }

    val patientList = MutableLiveData<List<PatientItem>>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun getPatients() {

        viewModelScope.launch {

            isLoading.postValue(true)

            try {
                val response = repository.getPatients()

                if (response.isSuccessful && response.body() != null) {

                    // ✅ Correct
                    patientList.postValue(response.body()!!)

                } else {
                    errorMessage.postValue("Error: ${response.code()}")
                }

            } catch (e: Exception) {

                errorMessage.postValue(e.message ?: "Something went wrong")

            } finally {

                isLoading.postValue(false)
            }
        }
    }

    private val _placeOrderResult = MutableLiveData<Resource<PlaceOrderResponse>>()
    val placeOrderResult: LiveData<Resource<PlaceOrderResponse>> = _placeOrderResult

    fun placeOrder(request: PlaceOrderRequest) {

        viewModelScope.launch {

            _placeOrderResult.value = Resource.Loading()

            try {
                val response = repository.placeOrder(request)

                if (response.isSuccessful && response.body() != null) {
                    _placeOrderResult.value = Resource.Success(response.body()!!)
                } else {
                    _placeOrderResult.value =
                        Resource.Error("Error: ${response.code()}")
                }

            } catch (e: Exception) {
                _placeOrderResult.value =
                    Resource.Error(e.message ?: "Something went wrong")
            }
        }
    }


//    my order
private val _orderList = MutableLiveData<Resource<List<OrderItem>>>()
    val orderList: LiveData<Resource<List<OrderItem>>> = _orderList

    fun getMyOrders() {

        viewModelScope.launch {

            _orderList.value = Resource.Loading()

            try {
                val response = repository.getMyOrders(1, 10)

                if (response.isSuccessful && response.body() != null) {
                    _orderList.value = Resource.Success(response.body()!!.data)
                } else {
                    _orderList.value = Resource.Error("Error: ${response.code()}")
                }

            } catch (e: Exception) {
                _orderList.value = Resource.Error(e.message ?: "Something went wrong")
            }
        }
    }

    private val _paymentResponse = MutableLiveData<Resource<PaymentResponse>>()
    val paymentResponse: LiveData<Resource<PaymentResponse>> = _paymentResponse
    fun initiatePayment(request: InitiatePaymentRequest) {

        viewModelScope.launch {

            _paymentResponse.postValue(Resource.Loading())

            try {

                val response = repository.initiatePayment(request)

                if (response.isSuccessful && response.body() != null) {

                    _paymentResponse.postValue(
                        Resource.Success(response.body()) as Resource<PaymentResponse>?
                    )

                } else {

                    _paymentResponse.postValue(
                        Resource.Error("Payment Failed")
                    )
                }

            } catch (e: Exception) {

                _paymentResponse.postValue(
                    Resource.Error(e.message ?: "Unknown Error")
                )
            }
        }
    }
}