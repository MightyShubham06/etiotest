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
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.factory.AddressViewModelFactory
import com.example.etiotest.data.model.AddressItem
import com.example.etiotest.data.viewmodel.AddressViewModel
import com.example.etiotest.databinding.SavedAddressFragmentBinding
import com.example.etiotest.ui.theme.adapter.AddressAdapter

class SavedAddressesFragment : Fragment() {

    private var _binding: SavedAddressFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AddressAdapter

    private val viewModel: AddressViewModel by viewModels {
        val apiService = RetrofitClient.getApiService(requireContext())
        AddressViewModelFactory(AuthRepository(apiService))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SavedAddressFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()
        observeData()

        viewModel.getAddresses()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddAddress.setOnClickListener {
            Toast.makeText(requireContext(), "Go to Add Address", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecycler() {
        adapter = AddressAdapter(

            onEditClick = {
                Toast.makeText(context, "Edit ${it.name}", Toast.LENGTH_SHORT).show()
            },

            onDeleteClick = {
                Toast.makeText(context, "Delete ${it.name}", Toast.LENGTH_SHORT).show()
            },

            // 🔥 MAIN FIX HERE
            onSelectClick = { address ->

                // ✅ Send selected address back
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_address", address)

                // ✅ Go back
                findNavController().popBackStack()
            }
        )

        binding.rvAddresses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SavedAddressesFragment.adapter
        }
    }

    private fun observeData() {
        viewModel.addressList.observe(viewLifecycleOwner) { res ->

            when (res) {

                is Resource.Loading -> {
                    // optional loader UI instead of toast
                }

                is Resource.Success -> {
                    val list = res.data ?: emptyList()

                    // ✅ FIXED (no unsafe casting)
                    adapter.submitList(list)
                }

                is Resource.Error -> {
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