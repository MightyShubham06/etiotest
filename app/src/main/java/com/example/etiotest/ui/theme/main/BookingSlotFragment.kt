package com.example.etiotest.ui.theme.main

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.etiotest.R
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.factory.SlotViewModelFactory
import com.example.etiotest.data.model.AddressItem
import com.example.etiotest.data.model.PatientItem
import com.example.etiotest.data.request.PlaceOrderRequest
import com.example.etiotest.data.request.TimeSlot
import com.example.etiotest.data.viewmodel.SlotViewModel
import com.example.etiotest.data.viewmodel.UserViewModel
import com.example.etiotest.databinding.DialogAddUserBinding
import com.example.etiotest.databinding.FragmentBookingSlotBinding
import java.text.SimpleDateFormat
import java.util.*

class BookingSlotFragment : Fragment(R.layout.fragment_booking_slot) {

    private var _binding: FragmentBookingSlotBinding? = null
    private val binding get() = _binding!!
    private var selectedAddress: AddressItem? = null

    private var selectedPatient: PatientItem? = null

    private val userViewModel: UserViewModel by viewModels {
        val apiService = RetrofitClient.getApiService(requireContext())
        UserViewModel.Factory(AuthRepository(apiService))
    }

    private val slotViewModel: SlotViewModel by viewModels {
        val apiService = RetrofitClient.getApiService(requireContext())
        SlotViewModelFactory(AuthRepository(apiService))
    }

    private var selectedDate = ""
    private var selectedSlot = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookingSlotBinding.bind(view)

        setupCalendar()
        observeSlots()
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<AddressItem>("selected_address")
            ?.observe(viewLifecycleOwner) { address ->

                selectedAddress = address   // 🔥 VERY IMPORTANT

                val fullAddress = listOfNotNull(
                    address.line1,
                    address.line2,
                    address.locality,
                    address.city,
                    address.state,
                    address.pincode
                ).joinToString(", ")

                binding.tvAddress.text = fullAddress
            }

        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<PatientItem>("selected_patient")
            ?.observe(viewLifecycleOwner) { patient ->

//                binding.tvPatientName.text = patient.name  // make sure this view exists

                selectedPatient = patient
            }
//        observeAddPatient()

        binding.btnSelectPatient.setOnClickListener {
            findNavController().navigate(
                R.id.action_bookingSlotFragment_to_patientListFragment
            )
        }
        binding.btnAddNewAddress.setOnClickListener {
            findNavController().navigate(R.id.action_booking_to_addAddress)
        }

        binding.btnContinue.setOnClickListener {

            val address = selectedAddress
            val patient = selectedPatient
            Log.d("BOOKING", "selectedAddress = $selectedAddress")
            Log.d("BOOKING", "addressId = ${selectedAddress?.id}")

            if (address?.id.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (patient?.id.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please select patient", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedDate.isEmpty() || selectedSlot.isEmpty()) {
                Toast.makeText(requireContext(), "Select date & slot", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val parts = selectedSlot.split("-")

            val request = PlaceOrderRequest(
                patientId = patient!!.id,
                addressId = address!!.id!!,   // now guaranteed non-null
                testIds = listOf("699171c686cd4eadb5634588"),
                bookingDate = "${selectedDate}T00:00:00.000Z",
                timeSlot = TimeSlot(
                    startTime = formatTo12Hour(parts[0]),
                    endTime = formatTo12Hour(parts[1]),
                    slotLabel = "${formatTo12Hour(parts[0])} - ${formatTo12Hour(parts[1])}"
                ),
                notes = "Patient fasting"
            )

            userViewModel.placeOrder(request)
        }
        binding.btnEdit.setOnClickListener {


            findNavController().navigate(
                R.id.action_bookingSlotFragment_to_savedAddressFragment
            )
        }
    }
    private fun observeAddPatient() {

        userViewModel.saveResult.observe(viewLifecycleOwner) { result ->

            result.onSuccess { response ->
                Toast.makeText(
                    requireContext(),
                    "Patient Added: ${response._id}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            result.onFailure {
                Toast.makeText(
                    requireContext(),
                    "Error: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupCalendar() {
        binding.calendarView.setOnDateChangeListener { _, year, month, day ->

            val cal = Calendar.getInstance()
            cal.set(year, month, day)

            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = format.format(cal.time)

            slotViewModel.getSlots(selectedDate)
        }
    }

    private fun observeSlots() {
        slotViewModel.slotsResponse.observe(viewLifecycleOwner) { res ->

            when (res) {
                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    val slots = res.data?.firstOrNull()?.availableSlots ?: emptyList()
                    showSlots(slots)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        userViewModel.placeOrderResult.observe(viewLifecycleOwner) { res ->

            when (res) {

                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Placing order...", Toast.LENGTH_SHORT).show()
                }

                is Resource.Success -> {

                    val order = res.data?.data

                    Toast.makeText(
                        requireContext(),
                        "Order Placed: ${order?.orderNumber}",
                        Toast.LENGTH_LONG
                    ).show()

                    // 👉 Navigate to success screen
                    // findNavController().navigate(R.id.orderSuccessFragment)
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showSlots(slots: List<String>) {

        binding.layoutSlots.removeAllViews()

        slots.forEach { slot ->

            val tv = layoutInflater.inflate(
                R.layout.item_slot,
                binding.layoutSlots,
                false
            ) as TextView

            tv.text = formatSlot(slot)

            tv.setOnClickListener {

                selectedSlot = slot

                for (i in 0 until binding.layoutSlots.childCount) {
                    binding.layoutSlots.getChildAt(i)
                        .setBackgroundResource(R.drawable.bg_outline_gray_box)
                }

                tv.setBackgroundResource(R.drawable.bg_selected_slot)
            }

            binding.layoutSlots.addView(tv)
        }
    }

    private fun formatSlot(slot: String): String {
        return try {
            val parts = slot.split("-")
            val input = SimpleDateFormat("HH:mm", Locale.getDefault())
            val output = SimpleDateFormat("hh:mm a", Locale.getDefault())

            val start = output.format(input.parse(parts[0])!!)
            val end = output.format(input.parse(parts[1])!!)

            "$start - $end"
        } catch (e: Exception) {
            slot
        }
    }


    private fun formatTo12Hour(time: String): String {
        return try {
            val input = SimpleDateFormat("HH:mm", Locale.getDefault())
            val output = SimpleDateFormat("hh:mm a", Locale.getDefault())
            output.format(input.parse(time)!!)
        } catch (e: Exception) {
            time
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}