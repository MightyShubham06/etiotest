package com.example.etiotest.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.LabTests
import com.example.etiotest.data.state.TestDetailState
import kotlinx.coroutines.launch

class TestDetailViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _detailState = MutableLiveData<TestDetailState>()
    val detailState: LiveData<TestDetailState> = _detailState

    fun fetchTestDetails(testId: String) {
        viewModelScope.launch {
            _detailState.value = TestDetailState.Loading
            try {
                val response = repository.getTestDetails(testId)
                if (response.isSuccessful && response.body() != null) {
                    val apiData = response.body()!!

                    // Convert DetailLabTest -> LabTests
                    val uiModel = LabTests(
                        id = apiData.id,
                        name = apiData.name,
                        description = apiData.description,
                        price = apiData.price,
                        originalPrice = apiData.originalPrice,
                        discountPercentage = apiData.discountPercentage,
                        duration = apiData.duration,
                        sampleType = apiData.sampleType,
                        preparation = apiData.preparation,
                        image = apiData.image,
                        displayPrice = apiData.displayPrice
                    )
                    _detailState.value = TestDetailState.Success(uiModel)
                } else {
                    _detailState.value = TestDetailState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _detailState.value = TestDetailState.Error(e.message ?: "Network error")
            }
        }
    }
}