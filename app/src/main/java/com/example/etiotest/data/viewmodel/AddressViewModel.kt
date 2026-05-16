package com.example.etiotest.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.model.AddressItem
import com.example.etiotest.data.model.AddressResponse
import kotlinx.coroutines.launch

class AddressViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _addressList = MutableLiveData<Resource<List<AddressItem>>>()
    val addressList: LiveData<Resource<List<AddressItem>>> = _addressList

    fun getAddresses() {
        viewModelScope.launch {

            _addressList.value = Resource.Loading()

            try {
                val response = repository.getAddresses()

                if (response.isSuccessful && response.body() != null) {
                    _addressList.value = Resource.Success(response.body()!!)
                } else {
                    _addressList.value =
                        Resource.Error("Error: ${response.code()} ${response.message()}")
                }

            } catch (e: Exception) {
                _addressList.value =
                    Resource.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }
}