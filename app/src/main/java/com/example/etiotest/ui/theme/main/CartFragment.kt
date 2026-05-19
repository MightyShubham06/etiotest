package com.example.etiotest.ui.theme.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.etiotest.R
import com.example.etiotest.data.viewmodel.CartViewModel
import com.example.etiotest.databinding.FragmentCartBinding
import com.example.etiotest.ui.theme.adapter.CartTestAdapter

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()

        binding.btnContinue.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_bookingSlotFragment)
        }
    }

    private fun setupRecyclerView() {
        val selectedAdapter = CartTestAdapter { test ->
            cartViewModel.removeFromCart(test.id)
        }
        binding.rvSelectedTests.adapter = selectedAdapter
        binding.rvSelectedTests.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() {
        cartViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            (binding.rvSelectedTests.adapter as CartTestAdapter).submitList(items)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}