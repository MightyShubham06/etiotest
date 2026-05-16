package com.example.etiotest.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.model.AddressRequest
import com.example.etiotest.data.viewmodel.UserViewModel
import com.example.etiotest.databinding.FragmentAddaddressBinding
import com.google.android.material.chip.Chip

class AddAddressFragment : Fragment() {

    private var _binding: FragmentAddaddressBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels {
        val apiService = RetrofitClient.getApiService(requireContext())
        UserViewModel.Factory(AuthRepository(apiService))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddaddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                val request = createAddressRequest()
                binding.btnSave.isEnabled = false // Prevent double clicks
                viewModel.saveAddress(request)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.addressSaveResult.observe(viewLifecycleOwner) { result ->
            binding.btnSave.isEnabled = true
            result.onSuccess {
                Toast.makeText(requireContext(), "Address saved successfully!", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        val fields = listOf(
            binding.etName to "Name is required",
            binding.etPhone to "Phone is required",
            binding.etLine1 to "Address Line 1 is required",
            binding.etCity to "City is required",
            binding.etPincode to "Pincode is required"
        )

        for ((view, message) in fields) {
            if (view.text.isNullOrEmpty()) {
                view.error = message
                isValid = false
            }
        }
        return isValid
    }

    private fun createAddressRequest(): AddressRequest {
        val selectedChipId = binding.chipGroupAddressType.checkedChipId
        val addressType = if (selectedChipId != View.NO_ID) {
            binding.root.findViewById<Chip>(selectedChipId).text.toString().lowercase()
        } else {
            "home"
        }

        return AddressRequest(
            name = binding.etName.text.toString(),
            phone = binding.etPhone.text.toString(),
            addressType = addressType,
            line1 = binding.etLine1.text.toString(),
            line2 = binding.etLine2.text.toString().takeIf { it.isNotEmpty() },
            locality = binding.etLocality.text.toString(),
            landmark = binding.etLandmark.text.toString().takeIf { it.isNotEmpty() },
            city = binding.etCity.text.toString(),
            district = binding.etDistrict.text.toString(),
            state = binding.etState.text.toString(),
            pincode = binding.etPincode.text.toString(),
            alternatePhone = binding.etAlternatePhone.text.toString().takeIf { it.isNotEmpty() }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}