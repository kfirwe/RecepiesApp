package com.example.finalproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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