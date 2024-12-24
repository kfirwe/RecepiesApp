package com.example.finalproject

import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRecipesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>() // Use mutable list for real data

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_recipes, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch user recipes from Firestore
        fetchUserRecipes()

        return view
    }

    private fun fetchUserRecipes() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid
        firestore.collection("recipes")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                recipes.clear()
                recipes.addAll(querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Recipe::class.java)?.copy(id = document.id)
                })

                // Update adapter with fetched recipes
                adapter = RecipeAdapter(recipes) { recipe ->
                    showUserRecipeDialog(recipe)
                }
                recyclerView.adapter = adapter
                recyclerView.adapter?.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("UserRecipesFragment", "Error fetching user recipes", exception)
                Toast.makeText(context, "Failed to load recipes", Toast.LENGTH_SHORT).show()
            }
    }



    private fun showUserRecipeDialog(recipe: Recipe) {
        val message = "User Recipe Clicked: ${recipe.title}"
        Log.d("UserRecipesFragment", message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
