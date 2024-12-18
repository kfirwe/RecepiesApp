package com.example.finalproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalRecipesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>() // Mutable list to append new recipes
    private var offset = 0 // Tracks current offset
    private val pageSize = 10 // Number of recipes per request
    private var isLoading = false // Prevent duplicate loading

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_global_recipes, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RecipeAdapter(recipes)
        recyclerView.adapter = adapter

        // Fetch initial recipes
        fetchRecipesFromAPI()

        // Add scroll listener for pagination
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItem == recipes.size - 1) {
                    fetchRecipesFromAPI() // Load next page
                }
            }
        })

        return view
    }

    private fun fetchRecipesFromAPI() {
        isLoading = true // Mark as loading to prevent duplicate calls
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getRecipes(
                    query = "", // Add query or other filters as needed
                    number = pageSize,
                    offset = offset,
                    apiKey = "018e269aad9f4e319ae2e37e383ec814"
                )

                if (offset >= response.totalResults) {
                    return@launch // Stop fetching more recipes
                }


                withContext(Dispatchers.Main) {
                    response.results?.let {
                        recipes.addAll(it)
                        adapter.notifyDataSetChanged()
                        offset += pageSize // Increment offset for next page
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false // Reset loading state
            }
        }
    }
}
