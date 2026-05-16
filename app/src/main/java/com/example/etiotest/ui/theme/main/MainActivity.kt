package com.example.etiotest.ui.theme.main

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.etiotest.R
import com.example.etiotest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment,
                R.id.splashFragment,
                R.id.signupFragment-> {
                    binding.cardBottomNav.visibility = View.GONE
                }
                else -> {
                    binding.cardBottomNav.visibility = View.VISIBLE
                    updateNavUI(destination.id)
                }
            }
        }

        // Click listeners
        binding.navHomeContainer.setOnClickListener {
            if (navController.currentDestination?.id != R.id.dashboardFragment) {
                navController.navigate(R.id.dashboardFragment)
            }
        }

        binding.navCartContainer.setOnClickListener {
            if (navController.currentDestination?.id != R.id.cartNewFragment) {
                navController.navigate(R.id.cartNewFragment)
            }
        }

        binding.navReportsContainer.setOnClickListener {

        }
    }

    private fun updateNavUI(destinationId: Int) {
        val navBlue = Color.parseColor("#3A93BD")
        val white = Color.WHITE

        binding.homeBackground.visibility = View.INVISIBLE
        binding.cartBackground.visibility = View.INVISIBLE
        binding.reportsBackground.visibility = View.INVISIBLE

        binding.navHome.setColorFilter(white)
        binding.navCart.setColorFilter(white)
        binding.navReports.setColorFilter(white)

        when (destinationId) {
            R.id.dashboardFragment -> {
                binding.homeBackground.visibility = View.VISIBLE
                binding.navHome.setColorFilter(navBlue)
            }
            R.id.cartNewFragment -> {
                binding.cartBackground.visibility = View.VISIBLE
                binding.navCart.setColorFilter(navBlue)
            }
            // R.id.reportsFragment -> {
            //     binding.reportsBackground.visibility = View.VISIBLE
            //     binding.navReports.setColorFilter(navBlue)
            // }
        }
    }
}