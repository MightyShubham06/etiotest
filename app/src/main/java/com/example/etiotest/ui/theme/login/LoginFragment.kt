package com.example.etiotest.ui.theme.login

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigator
import androidx.navigation.fragment.findNavController
import com.example.etiotest.R
import com.example.etiotest.data.AuthRepository
import com.example.etiotest.data.api.RetrofitClient
import com.example.etiotest.data.localdb.SessionManager
import com.example.etiotest.data.signup.VerifyOtpViewModel
import com.example.etiotest.data.state.VerifyOtpState
import com.example.etiotest.databinding.FragmentLoginBinding
import com.example.etiotest.ui.theme.auth.LoginState
import com.example.etiotest.ui.theme.auth.LoginViewModel
import com.google.android.material.tabs.TabLayout

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var verifyOtpViewModel: VerifyOtpViewModel
    private lateinit var loginViewModel: LoginViewModel
    private var otpDialog: AlertDialog? = null
    private lateinit var sessionManager: SessionManager
    private var isKeepMeLoggedInChecked = false



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI state

        val apiService = RetrofitClient.getApiService(requireContext())
        val repo = AuthRepository(apiService)
        sessionManager = SessionManager(requireContext())

        verifyOtpViewModel = VerifyOtpViewModel(repo)
        loginViewModel = LoginViewModel(repo)



        // Set initial state for the "Personal" tab as shown in image (index 1)
        // Or keep 0 if you want Corporate first.
        // The image shows Personal selected.
        binding.tabLayout.getTabAt(0)?.select()
        setupTabs()
        setupClickListeners()
        observeLoginState() // Add this
    }
    private fun observeLoginState() {
        loginViewModel.loginState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is LoginState.Loading -> {
                    binding.btnLogin.isEnabled = false
                    // Show a ProgressBar if you have one
                }
                is LoginState.Success -> {
                    binding.btnLogin.isEnabled = true
                    // Pass the jobId to the next fragment using SafeArgs
                    showOtpDialog(state.response.jobId)
//                    val action = LoginFragmentDirections.actionLoginToOtp(state.response.jobId)
//                    findNavController().navigate(action)
                }
                is LoginState.Error -> {
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
        verifyOtpViewModel.verifyState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is VerifyOtpState.Success -> {
                    // 3. Use the captured boolean in saveSession
                    sessionManager.saveSession(
                        accessToken = state.data.accessToken,
                        refreshToken = state.data.refreshToken,
                        userId = state.data.user.id,
                        name = state.data.user.name,
                        state.data.user.email,
                        state.data.user.phone,
                        keepMeLoggedIn = isKeepMeLoggedInChecked // Updated parameter
                    )

                    otpDialog?.dismiss()
                    Toast.makeText(requireContext(), "Welcome ${state.data.user.name}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_login_to_dashboard)
                }
                is VerifyOtpState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()

            // 2. Capture the switch state here
            isKeepMeLoggedInChecked = binding.switchAutoLogin.isChecked

            if (email.isNotEmpty()) {
                loginViewModel.login(email)
            } else {
                Toast.makeText(requireContext(), "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
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
    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    1 -> { // Corporate
                        binding.tilEmail.hint = "Enter your Corporate mail"
                        // Optional: Clear field on switch
                        binding.etEmail.text?.clear()
                    }
                    0 -> { // Personal
                        binding.tilEmail.hint = "Enter your Personal mail"
                        binding.etEmail.text?.clear()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
