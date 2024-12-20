package com.example.finalproject


import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog // Add this for ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.finalproject.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val uploadedRecipes = mutableListOf<Recipe>()
    private lateinit var adapter: RecipeAdapter
    private val PICK_IMAGE_REQUEST = 1

    // Loading Dialog
    private lateinit var loadingDialog: AlertDialog
    private var recipesFetched = false
    private var profileFetched = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupBottomNavigation()

        // Initialize loading dialog
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(R.layout.loading_spinner) // Create a layout file for a spinner
            .setCancelable(false)
            .create()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show loading spinner
        loadingDialog.show()

        // Initialize RecyclerView
        binding.recyclerViewUploadedRecipes.layoutManager = LinearLayoutManager(context)
        adapter = RecipeAdapter(uploadedRecipes, onRecipeClick = { recipe ->
            Toast.makeText(context, "Edit Recipe: ${recipe.title}", Toast.LENGTH_SHORT).show()
        })
        binding.recyclerViewUploadedRecipes.adapter = adapter

        // Fetch user's uploaded recipes
        fetchUploadedRecipes()

        // Fetch and set user profile data
        fetchUserProfile()

        setupButtons()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.root.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_profile

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.homeFragment)
                    true
                }
                R.id.nav_search -> {
                    Toast.makeText(context, "Search coming soon!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_add -> {
                    findNavController().navigate(R.id.addRecipeFragment)
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    private fun setupButtons() {
        binding.btnChangeProfilePicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        binding.btnUpdateDisplayName.setOnClickListener {
            val newName = binding.etDisplayName.text.toString()
            if (newName.isNotEmpty()) {
                updateDisplayName(newName)
            } else {
                Toast.makeText(context, "Display name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdateBio.setOnClickListener {
            val newBio = binding.etBio.text.toString()
            if (newBio.isNotEmpty()) {
                updateBio(newBio)
            } else {
                Toast.makeText(context, "Bio cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdatePassword.setOnClickListener {
            val newPassword = binding.etPassword.text.toString()
            if (newPassword.length >= 6) {
                updatePassword(newPassword)
            } else {
                Toast.makeText(context, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun fetchUploadedRecipes() {
        FirebaseUtils.getUploadedRecipes { recipes ->
            uploadedRecipes.clear()
            uploadedRecipes.addAll(recipes)
            adapter.notifyDataSetChanged()
            recipesFetched = true
            checkLoadingCompletion()
        }
    }

    private fun fetchUserProfile() {
        FirebaseUtils.getUserProfile { userProfile ->
            userProfile?.let {
                binding.etDisplayName.setText(it.displayName ?: "")
                binding.etBio.setText(it.bio ?: "")
                it.profilePictureUrl?.let { imageUrl ->
                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_placeholder_image)
                        .into(binding.ivProfilePicture)
                }
            }
            profileFetched = true
            checkLoadingCompletion()
        }
    }

    private fun checkLoadingCompletion() {
        if (recipesFetched && profileFetched) {
            loadingDialog.dismiss()
        }
    }

    private fun updateDisplayName(newName: String) {
        FirebaseUtils.updateDisplayName(newName) { success ->
            if (success) {
                Toast.makeText(context, "Display Name Updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to Update Display Name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateBio(newBio: String) {
        FirebaseUtils.updateBio(newBio) { success ->
            if (success) {
                Toast.makeText(context, "Bio Updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to Update Bio", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePassword(newPassword: String) {
        FirebaseUtils.updatePassword(newPassword) { success ->
            if (success) {
                Toast.makeText(context, "Password Updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to Update Password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data

            selectedImageUri?.let { uri ->
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                binding.ivProfilePicture.setImageURI(uri)

                FirebaseUtils.uploadProfilePicture(uri) { success ->
                    if (success) {
                        Toast.makeText(context, "Profile Picture Updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to Update Profile Picture", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
