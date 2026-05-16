package com.example.etiotest.ui.theme.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.etiotest.R
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.viewmodel.UserViewModel
import com.example.etiotest.databinding.FragmentOrdersBinding
import com.example.etiotest.ui.theme.adapter.OrdersAdapter

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OrdersAdapter

    private val viewModel: UserViewModel by viewModels {
        val api = RetrofitClient.getApiService(requireContext())
        UserViewModel.Factory(AuthRepository(api))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        observeData()

        viewModel.getMyOrders()
    }

    private fun setupRecycler() {
        adapter = OrdersAdapter { orderItem ->
            val bundle = Bundle().apply {
                putString("order_id", orderItem._id)
            }

            findNavController().navigate(
                R.id.action_ordersFragment_to_orderDetailsFragment,
                bundle
            )
        }

        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
    }

    private fun observeData() {
        viewModel.orderList.observe(viewLifecycleOwner) { res ->

            when (res) {

                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(res.data)
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, res.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}