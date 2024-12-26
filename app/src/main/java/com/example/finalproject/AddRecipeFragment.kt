package com.example.finalproject

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.*

class AddRecipeFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var uploadButton: Button
    private lateinit var selectedImageView: ImageView
    private var selectedImageUri: Uri? = null

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                // Display the selected image
                selectedImageView.visibility = View.VISIBLE
                selectedImageView.setImageURI(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        // Initialize views
        titleEditText = view.findViewById(R.id.etTitle)
        descriptionEditText = view.findViewById(R.id.etDescription)
        selectImageButton = view.findViewById(R.id.btnSelectImage)
        uploadButton = view.findViewById(R.id.btnUpload)
        selectedImageView = view.findViewById(R.id.ivSelectedImage)

        // Set up bottom navigation
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_add
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
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.ProfileFragment)
                    true
                }
                R.id.nav_add -> true
                else -> false
            }
        }

        // Set up image picker
        selectImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Set up upload button
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

        try {
            // Convert image to Base64
            val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            // Create recipe data
            val recipeId = UUID.randomUUID().toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val recipeData = hashMapOf(
                "id" to recipeId,
                "title" to title,
                "description" to description,
                "imageBase64" to base64Image,
                "userId" to userId,
                "comments" to emptyList<Map<String, Any>>() // Empty comments list
            )

            // Save to Firestore
            firestore.collection("recipes")
                .document(recipeId)
                .set(recipeData)
                .addOnSuccessListener {
                    Toast.makeText(context, "Recipe uploaded successfully!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.homeFragment)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to upload recipe.", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to process image.", Toast.LENGTH_SHORT).show()
        }
    }
}
