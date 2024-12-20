package com.example.finalproject

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddRecipeFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var uploadButton: Button
    private lateinit var selectedImageView: ImageView
    private var selectedImageUri: Uri? = null

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                selectedImageView.setImageURI(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        // Ensure the correct tab is selected in the BottomNavigationView
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_add

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
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.ProfileFragment)
                    true
                }
                R.id.nav_add -> {
                    // Already on profile, do nothing
                    true
                }
                else -> false
            }
        }

        titleEditText = view.findViewById(R.id.etTitle)
        descriptionEditText = view.findViewById(R.id.etDescription)
        selectImageButton = view.findViewById(R.id.btnSelectImage)
        uploadButton = view.findViewById(R.id.btnUpload)
        selectedImageView = view.findViewById(R.id.ivSelectedImage)

        selectImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        uploadButton.setOnClickListener {
            uploadRecipe()
        }

        return view
    }

    private fun uploadRecipe() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val recipeId = UUID.randomUUID().toString()
        val storageRef = storage.reference.child("recipes/$recipeId.jpg")

        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    val recipeData = hashMapOf(
                        "title" to title,
                        "description" to description,
                        "imageUrl" to imageUrl.toString(),
                        "comments" to emptyList<Map<String, Any>>() // Empty comments list
                    )
                    firestore.collection("recipes")
                        .document(recipeId)
                        .set(recipeData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Recipe uploaded successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to upload recipe.", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to upload image.", Toast.LENGTH_SHORT).show()
            }
    }
}
