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
import com.example.etiotest.data.viewmodel.PatientViewModel
import com.example.etiotest.databinding.FragmentPatientSelectionBinding
import com.example.etiotest.ui.theme.adapter.PatientNewAdapter

class PatientSelectionFragment : Fragment() {

    private var _binding: FragmentPatientSelectionBinding? = null
    private val binding get() = _binding!!

    private val patientViewModel: PatientViewModel by activityViewModels()

    private lateinit var patientAdapter: PatientNewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        patientAdapter = PatientNewAdapter { selectedPatient ->
            patientViewModel.selectPatient(selectedPatient)
            findNavController().popBackStack()
        }

        binding.rvPatients.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPatients.adapter = patientAdapter

        patientViewModel.patients.observe(viewLifecycleOwner) { list ->
            patientAdapter.updateList(list)
        }

        binding.btnAddNewPatient.setOnClickListener {
            val dialog = AddPatientDialog { newPatient ->
                patientViewModel.addPatient(newPatient)
            }
            dialog.show(parentFragmentManager, "AddPatientDialog")
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}