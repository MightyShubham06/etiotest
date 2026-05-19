package com.example.etiotest.ui.theme.main

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
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.viewmodel.CartState
import com.example.etiotest.data.viewmodel.CartViewModel
import com.example.etiotest.data.viewmodel.CartViewModelFactory
import com.example.etiotest.databinding.FragmentCartNewBinding
import com.example.etiotest.ui.theme.adapter.CartPackageAdapter

class CartNewFragment : Fragment() {

    private var _binding: FragmentCartNewBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by activityViewModels {
        CartViewModelFactory(AuthRepository(RetrofitClient.getApiService(requireContext())))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CartPackageAdapter(
            onRemoveClick = { test ->
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Remove Test")
                    .setMessage("Are you sure you want to remove ${test.name} from cart?")
                    .setPositiveButton("Remove") { _, _ ->
                        cartViewModel.removeFromCart(test.id)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            },
            onBookClick = { test ->
                findNavController().navigate(R.id.cartFragment)
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }

        cartViewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (items.isNullOrEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
                binding.bottomCheckoutBar.visibility = View.GONE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
                adapter.submitList(items)

                val total = items.sumOf { it.price ?: 0 }
                binding.tvTotalAmount.text = "Rs. $total"
                binding.bottomCheckoutBar.visibility = View.VISIBLE
            }
        }

        cartViewModel.cartState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CartState.Loading -> {}
                is CartState.AddSuccess -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    cartViewModel.fetchCartItems()
                }
                is CartState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        binding.btnCheckout.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }

        cartViewModel.fetchCartItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}