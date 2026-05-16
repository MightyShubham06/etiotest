// data/repository/AuthRepository.kt
package com.example.etiotest.data


import com.example.etiotest.data.model.AddToCartRequest
import com.example.etiotest.data.model.AddToCartResponse
import com.example.etiotest.data.model.AddressItem
import com.example.etiotest.data.model.AddressRequest
import com.example.etiotest.data.model.AddressResponse
import com.example.etiotest.data.model.CartViewResponse
import com.example.etiotest.data.model.CommonResponse
import com.example.etiotest.data.model.DetailLabTest
import com.example.etiotest.data.model.LoginResponse
import com.example.etiotest.data.model.MyOrdersResponse
import com.example.etiotest.data.model.OrderDetailResponse
import com.example.etiotest.data.model.OrderItem
import com.example.etiotest.data.model.PatientItem
import com.example.etiotest.data.model.PlaceOrderResponse
import com.example.etiotest.data.model.ProfileResponse
import com.example.etiotest.data.model.UpdateProfileRequest
import com.example.etiotest.data.model.UserProfileResponse
import com.example.etiotest.data.request.PlaceOrderRequest
import com.example.etiotest.data.request.UserRequest
import com.example.etiotest.data.request.UserResponse
import com.example.etiotest.data.request.VerifyOtpRequest
import com.example.etiotest.data.request.VerifyOtpResponse
import com.example.etiotest.data.signup.SignupResponse
import retrofit2.Response
import retrofit2.http.GET

class AuthRepository(private val api: ApiService) {

    suspend fun login(email: String): Response<LoginResponse> {
        return api.login(LoginRequest(email = email))
    }

    suspend fun signup(
        email: String,
        phone: String,
        name: String,
        userType: String
    ): Response<SignupResponse> {
        val req = SignupRequest(
            email = email,
            phone = phone,
            name = name,
            userType = userType
        )
        return api.signup(req)
    }

    suspend fun verifyOTP(
        otp: String,
        jobid: String
    ): Response<VerifyOtpResponse> {
        val req = VerifyOtpRequest(
            jobId = jobid,
            otp = otp
        )
        return api.verifyotp(req)
    }



    suspend fun getTests(page: Int, limit: Int): Response<TestListResponse> {
        return api.getTests(page, limit)
    }
    // In AuthRepository.kt
    suspend fun getTestDetails(testId: String): Response<DetailLabTest> {
        return api.getTestDetails(testId)
    }

    suspend fun getUserProfile(): Response<UserProfileResponse> {
        // Formats the token as a Bearer token automatically
        return api.getProfile()
    }
    suspend fun createUser(request: UserRequest): Response<UserResponse> {
        return api.createUser(request)
    }
    suspend fun addAddress(request: AddressRequest): Response<AddressResponse> {
        return api.createAddress(request)
    }

    suspend fun updateProfile(id: String, request: UpdateProfileRequest) =
        api.updateProfile(id, request)

    suspend fun getSlots(
        date: String
    ) = api.getSlots(
        startDate = date,
        endDate = date,
        startTime = "09:00",
        endTime = "18:00"
    )

    suspend fun getAddresses(): Response<List<AddressItem>> {
        return api.getAddresses()
    }
    suspend fun getPatients(): Response<List<PatientItem>> {
        return api.getPatients()
    }

    suspend fun placeOrder(request: PlaceOrderRequest): Response<PlaceOrderResponse> {
        return api.placeOrder(request)
    }
    suspend fun getMyOrders(page: Int, limit: Int): Response<MyOrdersResponse> {
        return api.getMyOrders(page, limit)
    }

    suspend fun getOrderById(orderId: String): Response<OrderDetailResponse> {
        return api.getOrderById(orderId)
    }

    suspend fun addToCart(testId: String) = api.addToCart(mapOf("testId" to testId))

    suspend fun getCart(): Response<CartViewResponse> = api.getCart()

    suspend fun removeFromCart(testId: String): Response<CommonResponse> {
        return api.removeFromCart(mapOf("testId" to testId))
    }

}

