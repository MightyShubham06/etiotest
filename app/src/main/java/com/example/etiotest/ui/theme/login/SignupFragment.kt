package com.example.etiotest.ui.theme.login

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.etiotest.R
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.localdb.SessionManager
import com.example.etiotest.data.signup.SignupState
import com.example.etiotest.data.signup.SignupViewModel
import com.example.etiotest.data.signup.VerifyOtpViewModel
import com.example.etiotest.data.state.VerifyOtpState
import com.example.etiotest.databinding.FragmentSignupBinding
import com.example.etiotest.ui.theme.LoaderDialog

class SignupFragment : Fragment() {
    private lateinit var loader: LoaderDialog
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SignupViewModel
    private lateinit var verifyOtpViewModel: VerifyOtpViewModel
    private lateinit var sessionManager: SessionManager

    private var otpDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loader = LoaderDialog(requireContext())


        // Use the context-based getter to avoid the "ApiService?" nullability mismatch
        val apiService = RetrofitClient.getApiService(requireContext())
        val repo = AuthRepository(apiService)


        viewModel = SignupViewModel(repo)
        verifyOtpViewModel = VerifyOtpViewModel(repo)
        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader = LoaderDialog(requireContext())

        binding.btnSignup.setOnClickListener { attemptSignup() }

        setupObservers()
    }

    private fun setupObservers() {
        // Observer for Signup (Initial step)
        viewModel.signupState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SignupState.Loading -> {
                    loader.show("Creating account...")

                    binding.btnSignup.isEnabled = false
                }
                is SignupState.Success -> {
                    loader.dismiss()

                    binding.btnSignup.isEnabled = true
                    showOtpDialog(state.data.jobId)
                    Toast.makeText(requireContext(), "OTP sent to ${state.data.email}", Toast.LENGTH_LONG).show()
                }
                is SignupState.Error -> {
                    loader.dismiss()

                    binding.btnSignup.isEnabled = true
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> { binding.btnSignup.isEnabled = true }
            }
        }

        // Observer for OTP Verification (Final step)
        verifyOtpViewModel.verifyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is VerifyOtpState.Loading -> {
                    // Optionally show loading in dialog
                }
                is VerifyOtpState.Success -> {
                    // 1. Store tokens and user info
                    sessionManager.saveSession(
                        state.data.accessToken,
                        state.data.refreshToken,
                        state.data.user.id,
                        state.data.user.name,
                        state.data.user.email,
                        state.data.user.phone
                    )

                    // 2. Dismiss dialog and navigate
                    otpDialog?.dismiss()
                    Toast.makeText(requireContext(), "Welcome ${state.data.user.name}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_signup_to_dashboard)
                }
                is VerifyOtpState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun showOtpDialog(jobId: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_otp, null)
        val otpBoxes = arrayOf(
            dialogView.findViewById<EditText>(R.id.otp1),
            dialogView.findViewById<EditText>(R.id.otp2),
            dialogView.findViewById<EditText>(R.id.otp3),
            dialogView.findViewById<EditText>(R.id.otp4),
            dialogView.findViewById<EditText>(R.id.otp5),
            dialogView.findViewById<EditText>(R.id.otp6)
        )
        val btnVerify = dialogView.findViewById<Button>(R.id.btnVerifyOtp)
        val tvSentTo = dialogView.findViewById<TextView>(R.id.tvOtpSentTo)

        tvSentTo.text = "We have sent a code to your email \n ${binding.etEmail.text}"

        otpDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        otpDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        setupOtpInputs(otpBoxes)

        btnVerify.setOnClickListener {
            val otpValue = otpBoxes.joinToString("") { it.text.toString() }
            if (otpValue.length == 6) {
                verifyOtpViewModel.verifyOtp(jobId, otpValue)
            } else {
                Toast.makeText(context, "Please enter 6 digit OTP", Toast.LENGTH_SHORT).show()
            }
        }

        otpDialog?.show()
    }

    private fun setupOtpInputs(boxes: Array<EditText>) {
        for (i in boxes.indices) {
            boxes[i].addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1 && i < boxes.size - 1) {
                        boxes[i + 1].requestFocus() // Move to next
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 0 && i > 0) {
                        boxes[i - 1].requestFocus() // Backspace logic
                    }
                }
            })
        }
    }
    private fun attemptSignup() {
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val name = binding.etName.text.toString()

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Valid email required"
            return
        }
        if (phone.length < 10) {
            binding.etPhone.error = "Valid phone required"
            return
        }
        if (name.isEmpty()) {
            binding.etName.error = "Name required"
            return
        }

        val userType = when (binding.rgUserType.checkedRadioButtonId) {
            binding.rbCorporate.id -> "corporate"
            binding.rbPersonal.id -> "personal"
            else -> "guest"
        }

        viewModel.signup(email, phone, name, userType)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        otpDialog = null
    }
}