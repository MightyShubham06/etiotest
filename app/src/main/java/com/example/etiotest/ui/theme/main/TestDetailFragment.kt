package com.example.etiotest.ui.theme.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.etiotest.R
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.LabTests
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.factory.TestDetailViewModelFactory
import com.example.etiotest.data.state.TestDetailState
import com.example.etiotest.data.viewmodel.CartState
import com.example.etiotest.data.viewmodel.CartViewModel
import com.example.etiotest.data.viewmodel.CartViewModelFactory
import com.example.etiotest.data.viewmodel.TestDetailViewModel
import com.example.etiotest.databinding.FragmentTestDetailBinding

class TestDetailFragment : Fragment() {

    private var _binding: FragmentTestDetailBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by activityViewModels {
        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = AuthRepository(apiService)

        CartViewModelFactory(repository)
    }
    private val viewModel: TestDetailViewModel by viewModels {
        // Manually create the dependencies
        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = AuthRepository(apiService)

        // Return the custom factory
        TestDetailViewModelFactory(repository)
    }

    // Ensure TestDetailFragmentArgs is generated from your nav_graph.xml
    private val args: TestDetailFragmentArgs by navArgs()
//    private val viewModel: TestDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTestDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateUI(args.selectedTest)

        setupClickListeners()
        setupObservers()

        viewModel.fetchTestDetails(args.selectedTest.id)
    }

    private fun setupObservers() {
        viewModel.detailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TestDetailState.Loading -> {
                }
                is TestDetailState.Success -> {
                    populateUI(state.test)
                }
                is TestDetailState.Error -> {
                    Toast.makeText(context, "Update failed: ${state.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        cartViewModel.cartState.observe(viewLifecycleOwner) { state ->
            if (state is CartState.AddSuccess) {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            } else if (state is CartState.Error) {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun populateUI(test: LabTests) {
        binding.tvTestTitle.text = test.name
        binding.tvPrice.text = "Rs. ${test.displayPrice}"
        binding.tvDuration.text = "Duration: ${test.duration}"
        binding.tvSample.text = "Sample: ${test.sampleType}"

        binding.tvPreparation.text = test.preparation.joinToString(separator = "\n") { "• $it" }

        binding.tvDisccount.text = "${test.discountPercentage}% OFF"
        binding.tvDisccount.visibility = if (test.discountPercentage > 0) View.VISIBLE else View.GONE
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddCart.setOnClickListener {
            val testId = args.selectedTest.id
            cartViewModel.addToCart(testId)
        }

        binding.btnBookNow.setOnClickListener {
            findNavController().navigate(R.id.action_detail_to_booking)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}