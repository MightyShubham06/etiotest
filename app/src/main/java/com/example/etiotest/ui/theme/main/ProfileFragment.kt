package com.example.etiotest.ui.theme.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.etiotest.R
import com.example.etiotest.data.localdb.SessionManager
import com.example.etiotest.databinding.FragmentProfileBinding
import com.example.etiotest.databinding.ItemProfileMenuBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfileBinding.bind(view)
        sessionManager = SessionManager(requireContext())

        setupUI()
        setupProfileMenu()
        setupListeners()
    }

    // ✅ Set user data
    private fun setupUI() {
        binding.tvProfileTitle.text =
            sessionManager.getUserName() ?: "Profile"
    }

    // ✅ Setup all menu items
    private fun setupProfileMenu() {

        configureMenuItem(
            binding.menuAbout,
            title = "About Us",
            iconRes = R.drawable.ic_location
        )

        configureMenuItem(
            binding.menuBlog,
            title = "Health Blog",
            iconRes = R.drawable.ic_healthblog
        )

        configureMenuItem(
            binding.menuContact,
            title = "Contact Us",
            iconRes = R.drawable.ic_contactus
        )

        configureMenuItem(
            binding.menuHelp,
            title = "Help",
            iconRes = R.drawable.ic_location
        )

        configureMenuItem(
            binding.menuOrders,
            title = "My Orders",
            iconRes = R.drawable.ic_orders // 🔥 use proper icon
        )
    }

    /**
     * ✅ Configure each menu item safely using ID (NOT title string)
     */
    private fun configureMenuItem(
        menuBinding: ItemProfileMenuBinding,
        title: String,
        iconRes: Int
    ) {
        menuBinding.tvMenuTitle.text = title
        menuBinding.ivIcon.setImageResource(iconRes)

        menuBinding.root.setOnClickListener {

            when (menuBinding.root.id) {

                binding.menuAbout.root.id -> {
                    // TODO: Navigate to About screen
                }

                binding.menuBlog.root.id -> {
                    // TODO: Navigate to Blog screen
                }

                binding.menuContact.root.id -> {
                    // TODO: Open contact screen / dialer
                }

                binding.menuHelp.root.id -> {
                    // TODO: Navigate to Help screen
                }

                binding.menuOrders.root.id -> {
                    findNavController().navigate(
                        R.id.action_profileFragment_to_ordersFragment
                    )
                }
            }
        }
    }

    // ✅ Other click listeners
    private fun setupListeners() {

        // Back
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            sessionManager.logout()

            findNavController().navigate(
                R.id.action_global_loginFragment
            )
        }

        // Edit profile
        binding.tvEditPhoto.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_profileDetailsFragment
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}