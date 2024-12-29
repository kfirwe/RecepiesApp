package com.example.finalproject.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.finalproject.R
import com.example.finalproject.ui.adapters.ViewPagerAdapter
import com.example.finalproject.viewmodels.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Setup ViewPager2 with tabs
        val adapter = ViewPagerAdapter(this)
        adapter.addFragment(viewModel.getUserRecipesFragment(), "User Recipes")
        adapter.addFragment(viewModel.getGlobalRecipesFragment(), "Global Recipes")

        viewPager.adapter = adapter

        // Attach TabLayout to ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        // Set Home as the default selected item in BottomNavigationView
        bottomNavigationView.selectedItemId = R.id.nav_home

        // Handle bottom navigation item clicks
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_search -> {
                    findNavController().navigate(R.id.SearchFragment)
                    true
                }
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.ProfileFragment)
                    true
                }
                R.id.nav_add -> {
                    findNavController().navigate(R.id.addRecipeFragment)
                    true
                }
                else -> false
            }
        }

        return view
    }
}
