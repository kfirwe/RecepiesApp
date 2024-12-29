package com.example.finalproject.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.R
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.databinding.FragmentProfileBinding
import com.example.finalproject.ui.adapters.RecipeAdapterForProfile
import com.example.finalproject.viewmodels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var adapter: RecipeAdapterForProfile
    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setupRecyclerView()
        setupObservers()
        setupButtons()
        setupBottomNavigation()

        // Initialize loading dialog
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(R.layout.loading_spinner)
            .setCancelable(false)
            .create()

        viewModel.fetchUserProfile()
        viewModel.fetchUserRecipes()

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewUploadedRecipes.layoutManager = LinearLayoutManager(context)
        adapter = RecipeAdapterForProfile(
            recipes = mutableListOf(),
            onEditClick = { recipe -> openEditDialog(recipe) },
            onDeleteClick = { recipe -> viewModel.deleteRecipe(recipe.id) },
            onChatClick = { recipe -> openCommentsDialog(recipe.id) }
        )
        binding.recyclerViewUploadedRecipes.adapter = adapter
        binding.recyclerViewUploadedRecipes.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.etDisplayName.setText(it.displayName)
                binding.etBio.setText(it.bio)
                if (!it.profilePictureBase64.isNullOrEmpty()) {
                    val imageBytes = Base64.decode(it.profilePictureBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    binding.ivProfilePicture.setImageBitmap(bitmap)
                }
            }
        }

        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateData(recipes)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) loadingDialog.show() else loadingDialog.dismiss()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun setupButtons() {
        binding.btnUpdateDisplayName.setOnClickListener {
            val newName = binding.etDisplayName.text.toString()
            if (newName.isNotEmpty()) {
                viewModel.updateDisplayName(newName)
            } else {
                Toast.makeText(context, "Display name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdateBio.setOnClickListener {
            val newBio = binding.etBio.text.toString()
            if (newBio.isNotEmpty()) {
                // Handle bio update (extend if necessary)
            } else {
                Toast.makeText(context, "Bio cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            // Handle logout and navigate to login screen
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.nav_profile
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.homeFragment)
                    true
                }
                R.id.nav_search -> {
                    findNavController().navigate(R.id.SearchFragment)
                    true
                }
                R.id.nav_add -> {
                    findNavController().navigate(R.id.addRecipeFragment)
                    true
                }
                R.id.nav_profile -> true // Already on Profile
                else -> false
            }
        }
    }

    private fun openEditDialog(recipe: Recipe) {
        val dialog = EditRecipeDialogFragment().apply {
            arguments = Bundle().apply {
                putString("recipeId", recipe.id)
                putString("title", recipe.title)
                putString("description", recipe.description)
                putString("imageBase64", recipe.imageBase64)
            }
        }

        dialog.setOnRecipeUpdatedListener(object : EditRecipeDialogFragment.OnRecipeUpdatedListener {
            override fun onRecipeUpdated(
                recipeId: String,
                updatedTitle: String,
                updatedDescription: String,
                updatedImageBase64: String?
            ) {
                // Prepare updated data for the recipe
                val updatedData = mutableMapOf<String, Any>(
                    "title" to updatedTitle,
                    "description" to updatedDescription
                )
                updatedImageBase64?.let { updatedData["imageBase64"] = it }

                // Use the ViewModel to update the recipe
                viewModel.updateRecipe(recipeId, updatedData)
            }
        })

        dialog.show(parentFragmentManager, "EditRecipeDialogFragment")
    }

    private fun openCommentsDialog(recipeId: String) {
        val dialog = CommentsDialogFragment().apply {
            arguments = Bundle().apply {
                putString("recipeId", recipeId)
                putBoolean("isProfileView", true) // Allow edit/delete for all comments
            }
        }
        dialog.show(parentFragmentManager, "CommentsDialogFragment")
    }


}
