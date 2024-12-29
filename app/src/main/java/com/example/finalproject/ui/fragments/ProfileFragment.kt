package com.example.finalproject.ui.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private val PICK_IMAGE_REQUEST_CODE = 101
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

        viewModel.passwordChangeStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
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
            val newName = binding.etDisplayName.text.toString().trim()
            if (newName.isNotEmpty()) {
                viewModel.updateDisplayName(newName)
            } else {
                Toast.makeText(context, "Display name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdatePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.btnChangeProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
        }


        binding.btnUpdateBio.setOnClickListener {
            val newBio = binding.etBio.text.toString().trim() // Get the bio input
            if (newBio.isNotEmpty()) {
                viewModel.updateUserBio(newBio) // Call the ViewModel method to handle the update
            } else {
                Toast.makeText(context, "Bio cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnLogout.setOnClickListener {
            // Handle logout and navigate to login screen
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data ?: return

            // Convert the image to Base64
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            // Update the profile picture through ViewModel
            viewModel.updateProfilePicture(base64Image)
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
        val editTextNewPassword: EditText = dialogView.findViewById(R.id.etNewPassword)

        AlertDialog.Builder(requireContext())
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val newPassword = editTextNewPassword.text.toString().trim()
                if (newPassword.length >= 6) {
                    viewModel.updatePassword(newPassword)
                } else {
                    Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
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
