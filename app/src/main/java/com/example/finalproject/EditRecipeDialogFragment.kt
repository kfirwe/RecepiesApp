//package com.example.finalproject
//
//import android.app.Activity
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.os.Bundle
//import android.util.Base64
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.fragment.app.DialogFragment
//import com.google.firebase.firestore.FirebaseFirestore
//import java.io.ByteArrayOutputStream
//
//class EditRecipeDialogFragment : DialogFragment() {
//
//    interface OnRecipeUpdatedListener {
//        fun onRecipeUpdated(recipeId: String, updatedTitle: String, updatedDescription: String, updatedImageBase64: String?)
//    }
//
//    private lateinit var titleEditText: EditText
//    private lateinit var descriptionEditText: EditText
//    private lateinit var selectImageButton: Button
//    private lateinit var updateButton: Button
//    private lateinit var selectedImageView: ImageView
//
//    private var selectedImageBase64: String? = null
//    private val firestore = FirebaseFirestore.getInstance()
//
//    private var listener: OnRecipeUpdatedListener? = null
//
//    private val imagePickerLauncher =
//        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//            uri?.let {
//                handleImageSelection(it)
//            }
//        }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val view = inflater.inflate(R.layout.fragment_edit_recipe_dialog, container, false)
//        titleEditText = view.findViewById(R.id.etEditTitle)
//        descriptionEditText = view.findViewById(R.id.etEditDescription)
//        selectImageButton = view.findViewById(R.id.btnSelectImage)
//        updateButton = view.findViewById(R.id.btnUpdateRecipe)
//        selectedImageView = view.findViewById(R.id.ivSelectedImage)
//
//        titleEditText.setText(arguments?.getString("title"))
//        descriptionEditText.setText(arguments?.getString("description"))
//
//        arguments?.getString("imageBase64")?.let { base64Image ->
//            val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
//            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//            selectedImageView.setImageBitmap(bitmap)
//        }
//
//        selectImageButton.setOnClickListener {
//            imagePickerLauncher.launch("image/*")
//        }
//
//        updateButton.setOnClickListener {
//            updateRecipe()
//        }
//
//        return view
//    }
//
//    private fun handleImageSelection(uri: Uri) {
//        try {
//            val inputStream = requireContext().contentResolver.openInputStream(uri)
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            inputStream?.close()
//
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
//            val imageBytes = byteArrayOutputStream.toByteArray()
//            selectedImageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT)
//
//            selectedImageView.setImageBitmap(bitmap)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun updateRecipe() {
//        val updatedTitle = titleEditText.text.toString().trim()
//        val updatedDescription = descriptionEditText.text.toString().trim()
//        val recipeId = arguments?.getString("recipeId") ?: return
//
//        if (updatedTitle.isEmpty() || updatedDescription.isEmpty()) {
//            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Use the existing Base64 image if no new image is selected
//        val updatedImageBase64 = selectedImageBase64 ?: arguments?.getString("imageBase64")
//
//        val updatedData = mapOf(
//            "title" to updatedTitle,
//            "description" to updatedDescription,
//            "imageBase64" to updatedImageBase64
//        )
//
//        firestore.collection("recipes").document(recipeId)
//            .update(updatedData)
//            .addOnSuccessListener {
//                Toast.makeText(context, "Recipe updated successfully", Toast.LENGTH_SHORT).show()
//                listener?.onRecipeUpdated(
//                    recipeId,
//                    updatedTitle,
//                    updatedDescription,
//                    updatedImageBase64
//                )
//                dismiss()
//            }
//            .addOnFailureListener {
//                Toast.makeText(context, "Failed to update recipe", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//
//    fun setOnRecipeUpdatedListener(listener: OnRecipeUpdatedListener) {
//        this.listener = listener
//    }
//}
