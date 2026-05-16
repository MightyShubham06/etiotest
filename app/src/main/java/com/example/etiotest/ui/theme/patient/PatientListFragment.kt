package com.example.etiotest.ui.patient

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.factory.UserViewModelFactory
import com.example.etiotest.databinding.FragmentPatientListBinding
import com.example.etiotest.databinding.DialogAddUserBinding
import com.example.etiotest.data.model.PatientItem
import com.example.etiotest.data.viewmodel.UserViewModel
import com.example.etiotest.ui.theme.adapter.PatientAdapter

class PatientListFragment : Fragment() {

    private var _binding: FragmentPatientListBinding? = null
    private val binding get() = _binding!!

    private val userViewModel: UserViewModel by viewModels {
        val apiService = RetrofitClient.getApiService(requireContext())
        UserViewModel.Factory(AuthRepository(apiService))
    }
    private lateinit var patientAdapter: PatientAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        setupObservers()

        // ✅ Load patient list
        userViewModel.getPatients()
    }

    // ✅ RecyclerView setup
    private fun setupRecyclerView() {

        patientAdapter = PatientAdapter { patient ->

            // ✅ Send selected patient back
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set("selected_patient", patient)

            findNavController().popBackStack()
        }

        binding.rvPatients.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = patientAdapter
            setHasFixedSize(true)
        }
    }

    // ✅ Click listeners
    private fun setupListeners() {

        binding.btnAddPatient.setOnClickListener {
            showAddUserDialog()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    // ✅ Observers
    private fun setupObservers() {

        userViewModel.patientList.observe(viewLifecycleOwner) { list ->
            patientAdapter.submitList(list)

            binding.lytEmptyState.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE

            binding.rvPatients.visibility =
                if (list.isEmpty()) View.GONE else View.VISIBLE
        }

        userViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

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

    // ✅ Your existing dialog reused
    private fun showAddUserDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogAddUserBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etName.text.toString()
            val phone = dialogBinding.etPhone.text.toString()
            val age = dialogBinding.etAge.text.toString()
            val gender = if (dialogBinding.rbMale.isChecked) "male" else "female"

            userViewModel.saveUserDetails(name, phone, age, gender)
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}