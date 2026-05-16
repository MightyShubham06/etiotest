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
import com.example.etiotest.data.model.SlotResponse
import com.example.etiotest.data.model.UpdateProfileRequest
import com.example.etiotest.data.model.UpdateProfileResponse
import com.example.etiotest.data.model.UserProfileResponse
import com.example.etiotest.data.request.PlaceOrderRequest
import com.example.etiotest.data.request.UserRequest
import com.example.etiotest.data.request.UserResponse
import com.example.etiotest.data.request.VerifyOtpRequest
import com.example.etiotest.data.request.VerifyOtpResponse
import com.example.etiotest.data.signup.SignupResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>


    @POST("auth/signup")
    suspend fun signup(@Body req: SignupRequest): Response<SignupResponse>

    @POST("auth/verify-otp")
    suspend fun verifyotp(@Body req: VerifyOtpRequest): Response<VerifyOtpResponse>

    @GET("tests") // Adjust the path as per your API documentation
    suspend fun getTests(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<TestListResponse>

    @POST("auth/refresh-token")
    fun refreshAccessToken(@Body refreshToken: String): Call<TokenRefreshResponse>

    @POST("addresses")
    suspend fun createAddress(@Body request: AddressRequest): Response<AddressResponse>

    @GET("addresses")
    suspend fun getAddresses(): Response<List<AddressItem>>
    @GET("tests/{id}")
    suspend fun getTestDetails(@Path("id") id: String): Response<DetailLabTest>

    @GET("api/profile")
    suspend fun getProfile(): Response<UserProfileResponse>
    @POST("patients")
    suspend fun createUser(
        @Body request: UserRequest
    ): Response<UserResponse>

    @PATCH("patients/{id}")
    suspend fun updateProfile(
        @Path("id") id: String,
        @Body request: UpdateProfileRequest
    ): Response<UpdateProfileResponse>

    @GET("slots")
    suspend fun getSlots(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("startTime") startTime: String,
        @Query("endTime") endTime: String
    ): Response<List<SlotResponse>>


    @GET("patients")
    suspend fun getPatients(): Response<List<PatientItem>>

    @POST("orders/create")
    suspend fun placeOrder(
        @Body request: PlaceOrderRequest
    ): Response<PlaceOrderResponse>

    @GET("orders/my-orders")
    suspend fun getMyOrders(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<MyOrdersResponse>

    @GET("orders/{id}")
    suspend fun getOrderById(
        @Path("id") id: String
    ): Response<OrderDetailResponse>

    @POST("cart/add")
    suspend fun addToCart(@Body req: Map<String, String>): Response<CommonResponse>

    @GET("cart/view")
    suspend fun getCart(): Response<CartViewResponse>

    @POST("cart/remove")
    suspend fun removeFromCart(@Body req: Map<String, String>): Response<CommonResponse>
}

data class CartViewResponse(
    val success: Boolean,
    val message: String,
    val data: List<LabTests>
)
