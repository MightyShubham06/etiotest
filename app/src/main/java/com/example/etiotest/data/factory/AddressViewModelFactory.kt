package com.example.etiotest.data.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.viewmodel.AddressViewModel

class AddressViewModelFactory(
    private val repository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddressViewModel(repository) as T
    }
}