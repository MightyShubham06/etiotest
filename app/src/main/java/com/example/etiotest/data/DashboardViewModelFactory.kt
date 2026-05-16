package com.example.etiotest.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.etiotest.data.viewmodel.DashboardViewModel

class DashboardViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}