package com.example.finalproject

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class UserRecipesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>()

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }

    private var isLoading = false
    private var isInitialLoad = true // Flag to check if it's the first load
    private var lastVisibleRecipe: DocumentSnapshot? = null
    private val limit = 10 // Number of recipes to load per batch

    private lateinit var loadingDialog: AlertDialog // Loading dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_recipes, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Initialize loading dialog
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(R.layout.loading_spinner) // Set the spinner layout
            .setCancelable(false)
            .create()

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Add divider decoration to the RecyclerView
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        adapter = RecipeAdapter(recipes) { recipe ->
            showUserRecipeDialog(recipe)
        }
        recyclerView.adapter = adapter

        // Set up lazy loading
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + 2)) {
                    loadMoreRecipes(showSpinner = false) // No spinner on scroll
                }
            }
        })

        // Fetch initial recipes
        loadMoreRecipes(showSpinner = true) // Show spinner on initial load

        return view
    }

    private fun loadMoreRecipes(showSpinner: Boolean) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true
        if (showSpinner && isInitialLoad) {
            loadingDialog.show() // Show loading spinner only for initial load
        }

        val userId = currentUser.uid
        var query = firestore.collection("recipes")
            .whereEqualTo("userId", userId)
            .limit(limit.toLong())

        lastVisibleRecipe?.let { documentSnapshot ->
            query = query.startAfter(documentSnapshot)
        }

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val newRecipes = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Recipe::class.java)?.copy(id = document.id)
                }

                recipes.addAll(newRecipes)
                adapter.notifyDataSetChanged()

                if (querySnapshot.documents.isNotEmpty()) {
                    lastVisibleRecipe = querySnapshot.documents.last()
                }

                isLoading = false
                if (showSpinner && isInitialLoad) {
                    loadingDialog.dismiss() // Hide spinner after initial load
                    isInitialLoad = false // Set flag to false after first load
                }
            }
            .addOnFailureListener { exception ->
                Log.e("UserRecipesFragment", "Error fetching recipes", exception)
                Toast.makeText(context, "Failed to load recipes", Toast.LENGTH_SHORT).show()
                isLoading = false
                if (showSpinner && isInitialLoad) {
                    loadingDialog.dismiss() // Hide spinner even on failure
                }
            }
    }

    private fun showUserRecipeDialog(recipe: Recipe) {
        val dialog = CommentsDialogFragment().apply {
            arguments = Bundle().apply {
                putString("recipeId", recipe.id)
            }
        }
        dialog.show(parentFragmentManager, "CommentsDialogFragment")
    }
}
