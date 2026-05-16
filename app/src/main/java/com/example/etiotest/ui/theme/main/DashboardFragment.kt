package com.example.mediora.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.etiotest.R
import com.example.etiotest.R.id.action_dashboard_to_cartFragment
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.DashboardViewModelFactory
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.state.TestListState
import com.example.etiotest.data.viewmodel.DashboardViewModel
import com.example.etiotest.databinding.FragmentDashboardBinding
import com.example.etiotest.ui.theme.adapter.TestsAdapter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private lateinit var testAdapter: TestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setup Dependencies
        val apiService = RetrofitClient.getApiService(requireContext())
        val repository = AuthRepository(apiService)
        val factory = DashboardViewModelFactory(repository)


        // 2. Initialize ViewModel with Factory
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        observeViewModel()


        // 3. Fetch Data
        viewModel.fetchTests()
        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_profileFragment)
        }


    }

    private fun setupRecyclerView() {
        // Initialize adapter only once
        testAdapter = TestsAdapter(emptyList()) { selectedTest ->
            // Pass the entire LabTests object via Safe Args
            val action = DashboardFragmentDirections.actionDashboardToDetail(selectedTest)
            findNavController().navigate(action)
        }

        binding.rvTestList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = testAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            viewModel.filterTests(text.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.testState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TestListState.Loading -> {
                    // Show a ProgressBar if you have one in your XML
                    // binding.progressBar.visibility = View.VISIBLE
                }
                is TestListState.Success -> {
                    // binding.progressBar.visibility = View.GONE
                    testAdapter.updateList(state.tests)
                }
                is TestListState.Error -> {
                    // binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}