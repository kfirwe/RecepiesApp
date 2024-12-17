package com.example.finalproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class UserRecipesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var recipes: MutableList<Recipe> // Use mutable list for mock data

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_recipes, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize mock data
        fetchMockUserRecipes()

        return view
    }

    private fun fetchMockUserRecipes() {
        // Initialize mock recipes
        recipes = mutableListOf(
            Recipe(
                id = "1",
                title = "Spaghetti Bolognese",
                description = "Classic Italian pasta dish with rich meat sauce.",
                imageUrl = "https://via.placeholder.com/300" // Placeholder URL
            ),
            Recipe(
                id = "2",
                title = "Vegan Salad",
                description = "Healthy and fresh vegan salad with avocado and quinoa.",
                imageUrl = "https://via.placeholder.com/300"
            ),
            Recipe(
                id = "3",
                title = "Chocolate Cake",
                description = "Delicious chocolate cake topped with creamy frosting.",
                imageUrl = "https://via.placeholder.com/300"
            )
        )


        // Initialize adapter and set it to RecyclerView
        adapter = RecipeAdapter(recipes)
        recyclerView.adapter = adapter
        recyclerView.adapter?.notifyDataSetChanged()

    }
}
