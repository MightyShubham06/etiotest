package com.example.etiotest.ui.theme.main

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.etiotest.commonutil.Resource
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.factory.DashboardViewModelFactory
import com.example.etiotest.data.model.UpdateProfileRequest
import com.example.etiotest.data.model.UserProfileResponse
import com.example.etiotest.data.viewmodel.DashboardViewModel
import com.example.etiotest.data.viewmodel.UserViewModel
import com.example.etiotest.databinding.PtofileDetailFragmentBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment to display and edit user profile details.
 * Maps data from the user profile JSON structure.
 */
class ProfileDetailsFragment : Fragment() {

    private var _binding: PtofileDetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels {
        val apiService = RetrofitClient.getApiService(requireContext())
        DashboardViewModelFactory(AuthRepository(apiService))
    }
    private val userViewModel: UserViewModel by viewModels {
        val apiService = RetrofitClient.getApiService(requireContext())
        UserViewModel.Factory(AuthRepository(apiService))
    }


    // Mock data based on provided JSON
    private val mockUserJson = """
        {
            "_id": "6999ac6ffa2374f7d5c541df",
            "email": "mightyshubham9055@gmail.com",
            "name": "John",
            "phone": "+919812345678",
            "userType": "personal",
            "createdAt": "2026-02-21T13:00:31.064Z",
            "updatedAt": "2026-04-14T17:06:44.261Z",
            "__v": 0,
            "dateOfBirth": "1990-05-15T00:00:00.000Z",
            "gender": "male",
            "profilePhoto": "https://etiotest.com/uploads/new-john.jpg"
        }
    """.trimIndent()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PtofileDetailFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()

        observeProfile()
        observeUpdateProfile()

        viewModel.fetchProfile()
    }
    private fun observeProfile() {

        viewModel.profileData.observe(viewLifecycleOwner) { user ->

            if (user != null) {
                bindProfileData(user)
            } else {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSave.setOnClickListener {

            val userId = viewModel.profileData.value?._id

            if (userId.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "User ID missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveProfile(userId)
        }
//        binding.fabEditPhoto.setOnClickListener {
//            Toast.makeText(requireContext(), "Edit photo clicked", Toast.LENGTH_SHORT).show()
//        }
        
        // Date Picker for DOB field
        binding.etDOB.setOnClickListener {
            // Show DatePickerDialog implementation here
        }
    }

    private fun bindProfileData(user: UserProfileResponse?) {

        Log.e(TAG, "bindProfileData: "+user )
        binding.etName.setText(user?.name)
        binding.etEmail.setText(user?.email)
        binding.etPhone.setText(user?.phone)
        binding.chipUserType.text = user?.userType?.uppercase()

        // ✅ DOB FORMAT FIX (ISO → UI)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

        try {
            val date = inputFormat.parse(user?.dateOfBirth)
            binding.etDOB.setText(outputFormat.format(date!!))
        } catch (e: Exception) {
            binding.etDOB.setText(user?.dateOfBirth)
        }

        // ✅ Glide Image
        Glide.with(this)
            .load(user?.profilePhoto)
            .placeholder(android.R.drawable.progress_horizontal)
            .error(android.R.drawable.ic_menu_report_image)
            .into(binding.ivProfilePhoto)
    }
    private fun observeUpdateProfile() {

        userViewModel.updateProfileResponse.observe(viewLifecycleOwner) { res ->

            when (res) {

                is Resource.Loading -> {
                    Toast.makeText(requireContext(), "Updating...", Toast.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    res.data?.let { user ->
                        binding.etName.setText(user.name)
                        binding.etPhone.setText(user.phone)

                        Toast.makeText(requireContext(), "Profile Updated!", Toast.LENGTH_SHORT).show()
                    }
                }

                is Resource.Error -> {
                    Toast.makeText(requireContext(), res.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun saveProfile(userId: String) {

        val name = binding.etName.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Name required", Toast.LENGTH_SHORT).show()
            return
        }

        val request = UpdateProfileRequest(
            name = name,
            age = 36 // improve later
        )

        userViewModel.updateProfile(userId, request)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}