package com.example.finalproject.viewmodels


import androidx.lifecycle.ViewModel
import com.example.finalproject.ui.fragments.GlobalRecipesFragment
import com.example.finalproject.ui.fragments.UserRecipesFragment

class HomeViewModel : ViewModel() {

    // Provides the User Recipes fragment
    fun getUserRecipesFragment() = UserRecipesFragment()

    // Provides the Global Recipes fragment
    fun getGlobalRecipesFragment() = GlobalRecipesFragment()
}
