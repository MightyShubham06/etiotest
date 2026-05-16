package com.example.etiotest.ui.theme.main

import android.R.attr.fontFamily
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.etiotest.R
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.model.OrderItem
import com.example.etiotest.data.viewmodel.UserViewModel
import com.example.etiotest.databinding.FragmentOrderDetailsBinding

class OrderDetailsFragment : Fragment() {

    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels {
        val api = RetrofitClient.getApiService(requireContext())
        UserViewModel.Factory(AuthRepository(api))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderId = arguments?.getString("order_id")

        if (orderId != null) {
            viewModel.getOrderDetailsById(orderId)
        } else {
            Toast.makeText(requireContext(), "Error: Order ID not found", Toast.LENGTH_SHORT).show()
        }

        observeOrderDetails()

        binding.ivBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun observeOrderDetails() {
        viewModel.orderDetail.observe(viewLifecycleOwner) { res ->
            when (res) {
                is Resource.Loading -> {
                    android.util.Log.d("DEBUG_ORDER", "Loading...")
                }
                is Resource.Success -> {
                    android.util.Log.d("DEBUG_ORDER", "Success: ${res.data}")
                    res.data?.let { populateUI(it) }
                }
                is Resource.Error -> {
                    android.util.Log.e("DEBUG_ORDER", "Error: ${res.message}")
                    Toast.makeText(context, "API Error: ${res.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun populateUI(item: OrderItem) {
        binding.patientName.text = item.patient.name
        val dateOnly = item.bookingDate.substring(0, 10)
        binding.pickupDateTime.text = "$dateOnly, ${item.timeSlot.slotLabel}"
        binding.totalFare.text = "Rs. ${item.pricing.total}"

        binding.llTestsListContainer.removeAllViews()
        item.tests.forEach { test ->
            val tv = TextView(requireContext()).apply {
                text = "• ${test.name}"
                textSize = 15f
                setTextColor(Color.parseColor("#000000"))
                setPadding(0, 8, 0, 8)
                typeface = ResourcesCompat.getFont(requireContext(), R.font.sf_medium)
            }
            binding.llTestsListContainer.addView(tv)
        }

        binding.llPriceBreakdownContainer.removeAllViews()
        item.tests.forEach { test ->
            val row = RelativeLayout(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 12, 0, 12)
            }

            val tvName = TextView(requireContext()).apply {
                text = "• ${test.name}"
                setTextColor(Color.BLACK)
                textSize = 14f
                typeface = ResourcesCompat.getFont(requireContext(), R.font.sf_medium)
            }

            val tvPrice = TextView(requireContext()).apply {
                text = "Rs. ${test.price}"
                setTextColor(Color.BLACK)
                textSize = 14f
                typeface = ResourcesCompat.getFont(requireContext(), R.font.sf_bold)

                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                }
                layoutParams = params
            }

            row.addView(tvName)
            row.addView(tvPrice)
            binding.llPriceBreakdownContainer.addView(row)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}