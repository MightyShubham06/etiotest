package com.example.etiotest.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.model.SlotResponse
import kotlinx.coroutines.launch

class SlotViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _slotsResponse = MutableLiveData<Resource<List<SlotResponse>>>()
    val slotsResponse: LiveData<Resource<List<SlotResponse>>> = _slotsResponse

    fun getSlots(date: String) {
        viewModelScope.launch {
            _slotsResponse.value = Resource.Loading()

            try {
                val res = repository.getSlots(date)

                if (res.isSuccessful && res.body() != null) {
                    _slotsResponse.value = Resource.Success(res.body()!!)
                } else {
                    _slotsResponse.value = Resource.Error(res.message())
                }

            } catch (e: Exception) {
                _slotsResponse.value = Resource.Error(e.message ?: "Error")
            }
        }
    }
}