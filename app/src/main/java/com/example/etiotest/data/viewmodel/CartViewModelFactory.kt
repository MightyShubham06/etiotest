package com.example.etiotest.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.viewmodel.CartViewModel

class CartViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}