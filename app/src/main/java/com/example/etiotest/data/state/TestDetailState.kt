package com.example.etiotest.data.state

import com.example.etiotest.data.LabTests
import com.example.etiotest.data.model.DetailLabTest

sealed class TestDetailState {
    object Loading : TestDetailState()
    data class Success(val test: LabTests) : TestDetailState()
    data class Error(val message: String) : TestDetailState()
}