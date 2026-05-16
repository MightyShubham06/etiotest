package com.example.etiotest.ui.theme.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.etiotest.R
import com.example.etiotest.data.localdb.SessionManager
import com.example.etiotest.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SplashViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sessionManager = SessionManager(requireActivity())

        // Observe navigation target from ViewModel
        if (sessionManager.isLoggedIn()) {
            findNavController().navigate(R.id.action_splash_to_dashboard)
        } else {
            findNavController().navigate(R.id.action_splash_to_login)
        }
//        viewModel.navigateTo.observe(viewLifecycleOwner, Observer { target ->
//            when (target) {
//                SplashNavTarget.LOGIN -> {
//                    findNavController().navigate(R.id.action_splash_to_login)
//                }
//                SplashNavTarget.MAIN -> {
//                    findNavController().navigate(R.id.action_splash_to_dashboard)
//                }
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
