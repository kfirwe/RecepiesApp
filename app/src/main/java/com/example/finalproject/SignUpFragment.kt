package com.example.finalproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.database.entities.UserImage
import com.example.finalproject.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import android.util.Base64
import java.io.ByteArrayOutputStream

class SignUpFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Handle Sign-Up button click
        val db = FirebaseFirestore.getInstance()

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmailSignUp.text.toString().trim()
            val password = binding.etPasswordSignUp.text.toString().trim()
            val name = binding.etNameSignUp.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            val user = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "createdAt" to System.currentTimeMillis()
                            )

                            db.collection("users").document(userId!!)
                                .set(user)
                                .addOnSuccessListener {
                                    uploadDefaultImageToFirestore(userId) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Sign-Up Successful! Welcome, $name", Toast.LENGTH_SHORT).show()
                                            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                                        } else {
                                            Toast.makeText(context, "Failed to upload default image", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                        } else {
                            Toast.makeText(context, "Sign-Up Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }

            } else {
                Toast.makeText(context, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to LoginFragment
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }
    }


    private fun uploadDefaultImageToFirestore(userId: String, onComplete: (Boolean) -> Unit) {
        val defaultImage = BitmapFactory.decodeResource(resources, R.drawable.default_user_profile)
        val outputStream = ByteArrayOutputStream()
        defaultImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val imageBytes = outputStream.toByteArray()

        // Convert to Base64 string
        val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)

        // Save to Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("profilePictureBase64", base64Image)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }


    private fun saveDefaultImageToRoom(userId: String) {
        val dao = AppDatabase.getDatabase(requireContext()).userImageDao()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Load default image into internal storage
                val defaultImageResId = R.drawable.ic_profile
                val imageFile = File(requireContext().filesDir, "$userId.png")

                withContext(Dispatchers.Main) {
                    Picasso.get()
                        .load(defaultImageResId)
                        .into(object : com.squareup.picasso.Target {
                            override fun onBitmapLoaded(bitmap: android.graphics.Bitmap, from: Picasso.LoadedFrom?) {
                                FileOutputStream(imageFile).use { out ->
                                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                                }
                                CoroutineScope(Dispatchers.IO).launch {
                                    // Save userKey and imagePath to Room
                                    val userImage = UserImage(userKey = userId, imagePath = imageFile.absolutePath)
                                    dao.insertUserImage(userImage)
                                }
                            }

                            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: android.graphics.drawable.Drawable?) {
                                Toast.makeText(context, "Failed to save default image", Toast.LENGTH_SHORT).show()
                            }

                            override fun onPrepareLoad(placeHolderDrawable: android.graphics.drawable.Drawable?) {}
                        })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
