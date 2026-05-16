package com.example.etiotest.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.LabTests
import com.example.etiotest.data.model.ProfileResponse
import com.example.etiotest.data.model.UserProfileResponse
import com.example.etiotest.data.state.TestListState
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _testState = MutableLiveData<TestListState>()
    val testState: LiveData<TestListState> = _testState

    private val _profileData = MutableLiveData<UserProfileResponse>()
    val profileData = MutableLiveData<UserProfileResponse>()

    private var fullTestList: List<LabTests> = emptyList() // Use LabTests

    fun fetchTests() {
        _testState.value = TestListState.Loading
        viewModelScope.launch {
            try {
                // IMPORTANT: Ensure your repository returns the wrapper
                // (e.g., TestListResponse) so you can access .tests
                val response = repository.getTests(page = 1, limit = 20)

                if (response.isSuccessful && response.body() != null) {
                    // Update this based on your actual API wrapper
                    fullTestList = response.body()!!.tests
                    _testState.value = TestListState.Success(fullTestList)
                } else {
                    _testState.value = TestListState.Error("Server error: ${response.code()}")
                }
            } catch (e: Exception) {
                _testState.value = TestListState.Error(e.localizedMessage ?: "Network error")
            }
        }
    }

    fun filterTests(query: String) {
        if (query.isEmpty()) {
            _testState.value = TestListState.Success(fullTestList)
        } else {
            val filtered = fullTestList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
            _testState.value = TestListState.Success(filtered)
        }
    }

    // --- New Profile State ---



    /**
     * Fetches user profile using the Bearer token passed from the fragment.
     */
    fun fetchProfile() {
        viewModelScope.launch {
            val res = repository.getUserProfile()
            if (res.isSuccessful) {
                profileData.postValue(res.body())
            }
        }
    }
}