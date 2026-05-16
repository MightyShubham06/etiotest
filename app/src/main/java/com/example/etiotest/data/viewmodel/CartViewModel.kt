package com.example.etiotest.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.LabTests
import com.example.etiotest.data.test.CartItem
import kotlinx.coroutines.launch

sealed class CartState {
    object Idle : CartState()
    object Loading : CartState()
    data class AddSuccess(val message: String) : CartState()
    data class CartLoaded(val items: List<LabTests>) : CartState()
    data class Error(val message: String) : CartState()
}

class CartViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _cartState = MutableLiveData<CartState>(CartState.Idle)
    val cartState: LiveData<CartState> = _cartState

    private val _cartItems = MutableLiveData<List<LabTests>>()
    val cartItems: LiveData<List<LabTests>> = _cartItems

    fun addToCart(testId: String) {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            try {
                val response = repository.addToCart(testId)
                if (response.isSuccessful) {
                    _cartState.value = CartState.AddSuccess("Added to Cart successfully")
                    fetchCartItems()
                } else {
                    _cartState.value = CartState.Error("Failed: ${response.code()}")
                }
            } catch (e: Exception) {
                _cartState.value = CartState.Error(e.message ?: "Network error")
            }
        }
    }

    fun fetchCartItems() {
        viewModelScope.launch {
            try {
                val response = repository.getCart()
                if (response.isSuccessful && response.body() != null) {
                    android.util.Log.d("CART_DATA", "Items received: ${response.body()?.data?.size}")

                    _cartItems.postValue(response.body()?.data ?: emptyList())
                }
            } catch (e: Exception) {
                android.util.Log.e("CART_DATA", "Error: ${e.message}")
            }
        }
    }

    fun removeFromCart(testId: String) {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            try {
                val response = repository.removeFromCart(testId)

                if (response.isSuccessful && response.body() != null) {
                    _cartState.value = CartState.AddSuccess("Removed from Cart successfully")

                    fetchCartItems()
                } else {
                    _cartState.value = CartState.Error("Failed to remove item")
                }
            } catch (e: Exception) {
                _cartState.value = CartState.Error(e.message ?: "Network error")
            }
        }
    }
}