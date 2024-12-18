package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.api.RetrofitClient.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalRecipesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private var recipes: MutableList<Recipe> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_global_recipes, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize adapter with an empty list
        adapter = RecipeAdapter(recipes)
        recyclerView.adapter = adapter

        // Fetch Recipes from API
        fetchRecipesFromAPI()

        return view
    }

    private fun fetchRecipesFromAPI() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getRecipes(
                    apiKey = "018e269aad9f4e319ae2e37e383ec814",
                    number = 5
                )

                if (response.isSuccessful) {
                    val fetchedRecipes = response.body()?.results ?: emptyList()
                    println("Full Response: ${response.body()}")
                    println("Fetched Recipes: $fetchedRecipes")

                    // Update the UI on the main thread
                    withContext(Dispatchers.Main) {
                        recipes.clear()
                        recipes.addAll(fetchedRecipes)
                        adapter.notifyDataSetChanged()
                    }
                } else {
                    // Log or handle API failure
                    println("API call failed: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                // Handle exceptions such as network errors
                println("Error during API call: ${e.message}")
            }
        }
    }





}
