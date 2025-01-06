package com.example.finalproject.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.data.repositories.RecipeRepository
import com.example.finalproject.database.AppDatabase
import com.example.finalproject.ui.adapters.RecipeAdapter
import com.example.finalproject.viewmodels.UserRecipesViewModel

class UserRecipesFragment : Fragment() {

    private lateinit var viewModel: UserRecipesViewModel
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_user_recipes, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Initialize ViewModel with repository
        val recipeDao = AppDatabase.getDatabase(requireContext()).recipeDao()
        val repository = RecipeRepository(recipeDao)
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.NewInstanceFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return UserRecipesViewModel(repository) as T
                }
            }
        )[UserRecipesViewModel::class.java]

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RecipeAdapter(recipes) { recipe ->
            showCommentsDialog(recipe)
        }
        recyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        // Initialize loading dialog
        loadingDialog = AlertDialog.Builder(requireContext())
            .setView(R.layout.loading_spinner)
            .setCancelable(false)
            .create()

        setupObservers()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var lastScrollDirection = 0 // Track scroll direction (1 = down, -1 = up)

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // Detect scroll direction
                val currentScrollDirection = if (dy > 0) 1 else if (dy < 0) -1 else 0

                // Load more recipes only when scrolling down
                if (currentScrollDirection == 1 && lastVisibleItem == recipes.size - 1) {
                    viewModel.loadRecipes()
                }

                // Update last scroll direction
                lastScrollDirection = currentScrollDirection
            }
        })


        // Load initial recipes
        viewModel.loadRecipes()

        return view
    }

    private fun setupObservers() {
        viewModel.recipes.observe(viewLifecycleOwner) { newRecipes ->
            recipes.addAll(newRecipes) // Append new recipes
            adapter.notifyDataSetChanged() // Notify adapter of data changes
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) loadingDialog.show() else loadingDialog.dismiss()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun showCommentsDialog(recipe: Recipe) {
        val dialog = CommentsDialogFragment().apply {
            arguments = Bundle().apply {
                putString("recipeId", recipe.id)
            }
        }
        dialog.show(parentFragmentManager, "CommentsDialogFragment")
    }
}
