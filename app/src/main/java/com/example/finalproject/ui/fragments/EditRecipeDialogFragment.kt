package com.example.finalproject.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.finalproject.R
import com.example.finalproject.viewmodels.EditRecipeViewModel
import java.io.ByteArrayOutputStream

class EditRecipeDialogFragment : DialogFragment() {

    interface OnRecipeUpdatedListener {
        fun onRecipeUpdated(
            recipeId: String,
            updatedTitle: String,
            updatedDescription: String,
            updatedImageBase64: String?
        )
    }

    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var selectImageButton: Button
    private lateinit var updateButton: Button
    private lateinit var selectedImageView: ImageView

    private var listener: OnRecipeUpdatedListener? = null
    private var recipeId: String = ""
    private var selectedImageBase64: String? = null

    private lateinit var viewModel: EditRecipeViewModel

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { handleImageSelection(it) }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeId = arguments?.getString("recipeId") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_edit_recipe_dialog, container, false)

        titleEditText = view.findViewById(R.id.etEditTitle)
        descriptionEditText = view.findViewById(R.id.etEditDescription)
        selectImageButton = view.findViewById(R.id.btnSelectImage)
        updateButton = view.findViewById(R.id.btnUpdateRecipe)
        selectedImageView = view.findViewById(R.id.ivSelectedImage)

        titleEditText.setText(arguments?.getString("title"))
        descriptionEditText.setText(arguments?.getString("description"))

        arguments?.getString("imageBase64")?.let { base64Image ->
            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            selectedImageView.setImageBitmap(bitmap)
        }

        // Manually initialize the ViewModel
        val appContext = requireContext().applicationContext
        viewModel = EditRecipeViewModel(appContext)

        selectImageButton.setOnClickListener {
            requestStoragePermissionIfNeeded()
//            imagePickerLauncher.launch("image/*")
        }

        updateButton.setOnClickListener { updateRecipe() }

        return view
    }

//    private fun handleImageSelection(uri: Uri) {
//        try {
//            // Open an InputStream for the selected image
//            val inputStream = requireContext().contentResolver.openInputStream(uri)
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            inputStream?.close()
//
//            // Convert the Bitmap to a Base64-encoded string
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
//            val imageBytes = byteArrayOutputStream.toByteArray()
//            selectedImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
//
//            // Display the selected image in the ImageView
//            selectedImageView.setImageBitmap(bitmap)
//            selectedImageView.visibility = View.VISIBLE
//        } catch (e: SecurityException) {
//            e.printStackTrace()
//            Toast.makeText(context, "Permission denied. Please check your app's settings.", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(context, "Failed to load image.", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun handleImageSelection(uri: Uri) {
        try {
            // Open an InputStream for the selected image
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Compress the image to reduce its size
            val compressedBitmap = compressBitmap(originalBitmap, maxFileSizeInKB = 1024)

            // Convert the compressed Bitmap to a Base64-encoded string
            val byteArrayOutputStream = ByteArrayOutputStream()
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream) // Adjust quality if needed
            val imageBytes = byteArrayOutputStream.toByteArray()
            selectedImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            // Display the compressed image in the ImageView
            selectedImageView.setImageBitmap(compressedBitmap)
            selectedImageView.visibility = View.VISIBLE

            println("Compressed image size: ${imageBytes.size / 1024} KB") // Log the compressed size
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(context, "Permission denied. Please check your app's settings.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to load image.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun compressBitmap(original: Bitmap, maxFileSizeInKB: Int): Bitmap {
        var quality = 100
        var byteArrayOutputStream = ByteArrayOutputStream()

        // Compress the image in a loop, reducing quality until it meets the size requirement
        original.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        while (byteArrayOutputStream.toByteArray().size / 1024 > maxFileSizeInKB && quality > 10) {
            quality -= 10
            byteArrayOutputStream = ByteArrayOutputStream()
            original.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        }

        val byteArray = byteArrayOutputStream.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }



    private fun requestStoragePermissionIfNeeded() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            shouldShowRequestPermissionRationale(permission) -> {
                Toast.makeText(context, "Storage permission is required to select an image.", Toast.LENGTH_SHORT).show()
            }
            requireContext().checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted
                imagePickerLauncher.launch("image/*")
            }
            else -> {
                // Request permission
                requestPermissions(arrayOf(permission), REQUEST_STORAGE_PERMISSION)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                imagePickerLauncher.launch("image/*")
            } else {
                // Permission denied
                Toast.makeText(context, "Storage permission denied. Cannot select an image.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private companion object {
        const val REQUEST_STORAGE_PERMISSION = 101
    }





    private fun updateRecipe() {
        val updatedTitle = titleEditText.text.toString().trim()
        val updatedDescription = descriptionEditText.text.toString().trim()

        if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mutableMapOf<String, Any>(
            "title" to updatedTitle,
            "description" to updatedDescription
        )

        selectedImageBase64?.let {
            updatedData["imageBase64"] = it
        }

        println("Updating recipe with ID: $recipeId and data: $updatedData") // Log the update details

        viewModel.updateRecipe(recipeId, updatedData).observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Recipe updated successfully", Toast.LENGTH_SHORT).show()
                listener?.onRecipeUpdated(recipeId, updatedTitle, updatedDescription, selectedImageBase64)
                dismiss()
            } else {
                Toast.makeText(context, "Failed to update recipe", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun setOnRecipeUpdatedListener(listener: OnRecipeUpdatedListener) {
        this.listener = listener
    }
}
