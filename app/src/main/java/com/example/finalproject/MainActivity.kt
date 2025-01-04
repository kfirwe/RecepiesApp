package com.example.finalproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.finalproject.data.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var authRepository: AuthRepository
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ensure the navController is properly initialized
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        authRepository = AuthRepository(this)

        lifecycleScope.launch {
            val savedUser = authRepository.getLoggedInUser()
            if (savedUser != null && FirebaseAuth.getInstance().currentUser != null) {
                navigateToHomeScreen()
            } else {
                navigateToLoginScreen()
            }
        }
    }

    private fun navigateToHomeScreen() {
        navController.navigate(R.id.homeFragment)
    }

    private fun navigateToLoginScreen() {
        navController.navigate(R.id.loginFragment)
    }
}



//import androidx.core.view.WindowCompat
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // Enable edge-to-edge rendering
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//
//        setContentView(R.layout.activity_main)
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        // Ensure proper navigation handling if needed
//        return super.onSupportNavigateUp() || onBackPressedDispatcher.hasEnabledCallbacks()
//    }
//}


//class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        // Check if the savedInstanceState is null to avoid adding the fragment multiple times
//        if (savedInstanceState == null) {
//            supportFragmentManager.commit {
//                setReorderingAllowed(true)
//                add(R.id.fragment_container_view, LoginFragment())
//            }
//        }
//    }
//}