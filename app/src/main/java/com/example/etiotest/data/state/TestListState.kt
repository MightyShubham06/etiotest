package com.example.etiotest.data.state

import androidx.lifecycle.*
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.LabTests
import com.example.etiotest.data.model.ProfileResponse
import kotlinx.coroutines.launch

sealed class TestListState {
    object Loading : TestListState()
    data class Success(val tests: List<LabTests>) : TestListState() // Use LabTests
    data class Error(val message: String) : TestListState()
}

