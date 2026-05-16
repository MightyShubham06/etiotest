package com.example.etiotest.ui.theme.main

import DashboardViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.etiotest.R
import com.example.etiotest.data.LabTests
import com.example.etiotest.data.viewmodel.CartViewModel
import com.example.etiotest.data.viewmodel.PatientViewModel
import com.example.etiotest.databinding.FragmentCartBinding
import com.example.etiotest.ui.theme.adapter.CartAdapter
import com.example.etiotest.ui.theme.adapter.CartTestAdapter
import com.example.etiotest.ui.theme.adapter.RecommendedAdapter

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by activityViewModels()
    private val patientViewModel: PatientViewModel by activityViewModels()
    private lateinit var recommendedAdapter: RecommendedAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeData()

        val dummyRecommended = listOf(
            LabTests(
                id = "1",
                name = "Thyroid Test",
                description = "Basic thyroid check",
                price = 400,
                originalPrice = 500,
                discountPercentage = 20,
                duration = "24 hrs",
                sampleType = "Blood",
                preparation = emptyList(),
                image = "",
                displayPrice = 400
            ),
            LabTests(
                id = "2",
                name = "Vitamin D Test",
                description = "Vitamin D level test",
                price = 500,
                originalPrice = 600,
                discountPercentage = 15,
                duration = "24 hrs",
                sampleType = "Blood",
                preparation = emptyList(),
                image = "",
                displayPrice = 500
            ),
            LabTests(
                id = "3",
                name = "Kidney Test",
                description = "Kidney function test",
                price = 600,
                originalPrice = 700,
                discountPercentage = 10,
                duration = "24 hrs",
                sampleType = "Blood",
                preparation = emptyList(),
                image = "",
                displayPrice = 600
            )
        )

        recommendedAdapter.submitList(dummyRecommended)

        binding.btnPatientSelection.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_patientSelectionFragment)
        }

        binding.btnContinue.setOnClickListener {
            if (patientViewModel.selectedPatient.value == null) {
                Toast.makeText(context, "Please select a patient first!", Toast.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(R.id.action_cartFragment_to_bookingSlotFragment)
            }
        }

        binding.btnDeleteDetails.setOnClickListener {
            patientViewModel.selectPatient(null)
            binding.cardPatientDetails.visibility = View.GONE
        }
    }

    private fun setupRecyclerViews() {

        val selectedAdapter = CartTestAdapter { test ->

            cartViewModel.removeFromCart(test.id)
        }
        binding.rvSelectedTests.adapter = selectedAdapter
        binding.rvSelectedTests.layoutManager = LinearLayoutManager(requireContext())

        // Recommended tests
        recommendedAdapter = RecommendedAdapter { test ->
            cartViewModel.addToCart(test.id)

            Toast.makeText(context, "${test.name} added!", Toast.LENGTH_SHORT).show()
        }

        binding.rvRecommendedTests.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvRecommendedTests.adapter = recommendedAdapter
    }

    private fun observeData() {
        cartViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            (binding.rvSelectedTests.adapter as CartTestAdapter).submitList(items)
        }

        patientViewModel.selectedPatient.observe(viewLifecycleOwner) { patient ->
            if (patient != null) {
                binding.cardPatientDetails.visibility = View.VISIBLE
                binding.tvPatientName.text = "Name : ${patient.name}"
                binding.tvPatientPhone.text = "Phone : ${patient.phone}"
                binding.tvPatientAge.text = "Age : ${patient.age}"
                binding.tvPatientGender.text = "Gender : ${patient.gender}"
            } else {
                binding.cardPatientDetails.visibility = View.GONE
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}