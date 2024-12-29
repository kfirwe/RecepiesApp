package com.example.finalproject.ui.fragments


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.finalproject.R
import com.example.finalproject.viewmodels.AddRecipeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.ByteArrayOutputStream

class AddRecipeFragment : Fragment() {

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var uploadButton: Button
    private lateinit var selectedImageView: ImageView
    private lateinit var progressBar: ProgressBar

    private var selectedImageUri: Uri? = null
    private lateinit var viewModel: AddRecipeViewModel

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                selectedImageView.visibility = View.VISIBLE
                selectedImageView.setImageURI(uri)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_recipe, container, false)

        titleEditText = view.findViewById(R.id.etTitle)
        descriptionEditText = view.findViewById(R.id.etDescription)
        selectImageButton = view.findViewById(R.id.btnSelectImage)
        uploadButton = view.findViewById(R.id.btnUpload)
        selectedImageView = view.findViewById(R.id.ivSelectedImage)
        progressBar = view.findViewById(R.id.progressBar)

        setupBottomNavigation(view)

        viewModel = ViewModelProvider(this)[AddRecipeViewModel::class.java]

        selectImageButton.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        uploadButton.setOnClickListener {
            uploadRecipe()
        }

        observeViewModel()

        return view
    }

    private fun setupBottomNavigation(view: View) {
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
    }

    private fun uploadRecipe() {
        val title = titleEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || selectedImageUri == null) {
            Toast.makeText(context, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val inputStream = requireContext().contentResolver.openInputStream(selectedImageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val imageBytes = byteArrayOutputStream.toByteArray()
            val base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)

            viewModel.uploadRecipe(title, description, base64Image)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to process image.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.uploadSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Recipe uploaded successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.homeFragment)
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
