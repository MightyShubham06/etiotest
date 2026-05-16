package com.example.etiotest.data.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.viewmodel.TestDetailViewModel

class TestDetailViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TestDetailViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}