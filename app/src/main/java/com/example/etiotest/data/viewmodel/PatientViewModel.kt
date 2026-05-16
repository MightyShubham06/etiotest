package com.example.etiotest.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.etiotest.data.model.PatientNew

class PatientViewModel : ViewModel() {

    private val _patients = MutableLiveData<MutableList<PatientNew>>(
        mutableListOf(
            PatientNew(1, "Mr. Nikhil Kalvekar", "9876543210", "28", "Male"),
            PatientNew(2, "Mr. Rahul Sharma", "9123456780", "32", "Male"),
            PatientNew(3, "Mr. Shubham K.", "9136784940", "29", "Male")
        )
    )
    val patients: LiveData<MutableList<PatientNew>> = _patients

    private val _selectedPatient = MutableLiveData<PatientNew?>()
    val selectedPatient: LiveData<PatientNew?> = _selectedPatient

    fun addPatient(patient: PatientNew) {
        val list = _patients.value ?: mutableListOf()
        list.add(patient)
        _patients.value = list
    }

    fun selectPatient(patient: PatientNew?) {
        _selectedPatient.value = patient
    }

    fun clearSelectedPatient() {
        _selectedPatient.value = null
    }
}