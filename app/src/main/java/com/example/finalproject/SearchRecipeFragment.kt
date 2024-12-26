package com.example.finalproject

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SearchRecipeFragment : Fragment() {

    private lateinit var searchQueryEditText: EditText
    private lateinit var searchButton: ImageButton
    private lateinit var searchBySpinner: Spinner
    private lateinit var conditionSpinner: Spinner
    private lateinit var limitEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter

    private val recipes = mutableListOf<Recipe>()
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_recipe, container, false)

        searchQueryEditText = view.findViewById(R.id.etSearchQuery)
        searchButton = view.findViewById(R.id.btnSearch)
        searchBySpinner = view.findViewById(R.id.spinnerSearchBy)
        conditionSpinner = view.findViewById(R.id.spinnerCondition)
        limitEditText = view.findViewById(R.id.etLimit)
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults)

        // Set up bottom navigation
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_search
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.homeFragment)
                    true
                }
                R.id.nav_add -> {
                    findNavController().navigate(R.id.addRecipeFragment)
                    true
                }
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.ProfileFragment)
                    true
                }
                R.id.nav_search -> true
                else -> false
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeAdapter(recipes) { recipe ->
            showCommentsDialog(recipe) // Navigate to comments dialog on recipe click
        }
        recyclerView.adapter = adapter

        // Add divider decoration to the RecyclerView
        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)


        searchButton.setOnClickListener {
            performSearch()
        }

        return view
    }

    private fun performSearch() {
        val queryText = searchQueryEditText.text.toString().trim()
        if (queryText.isEmpty()) {
            Toast.makeText(requireContext(), "Enter a search query", Toast.LENGTH_SHORT).show()
            return
        }

        val searchBy = when (searchBySpinner.selectedItem.toString()) {
            "Title" -> "title"
            "Description" -> "description"
            else -> "title"
        }

        val condition = conditionSpinner.selectedItem.toString()

        // Handle limit validation
        val limitInput = limitEditText.text.toString()
        val limit = limitInput.toIntOrNull()
        if (limit == null || limit <= 0) {
            Toast.makeText(
                requireContext(),
                "Please enter a valid positive number for the limit",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        var query: Query = firestore.collection("recipes")

        query = when (condition) {
            "Starts With" -> query.whereGreaterThanOrEqualTo(searchBy, queryText)
                .whereLessThan(searchBy, queryText + '\uf8ff')
            "Exact Match" -> query.whereEqualTo(searchBy, queryText)
            else -> query
        }

        query.limit(limit.toLong()).get()
            .addOnSuccessListener { snapshot ->
                recipes.clear()
                recipes.addAll(snapshot.documents.mapNotNull { it.toObject(Recipe::class.java) })
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Search failed: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showCommentsDialog(recipe: Recipe) {
        val dialog = CommentsDialogFragment().apply {
            arguments = Bundle().apply {
                putString("recipeId", recipe.id)
                putBoolean("isOwner", false) // Allow only editing/deleting own comments
            }
        }
        dialog.show(parentFragmentManager, "CommentsDialogFragment")
    }
}
