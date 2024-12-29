package com.example.finalproject.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.ui.adapters.RecipeAdapter
import com.example.finalproject.viewmodels.UserRecipesViewModel

class UserRecipesFragment : Fragment() {

    private val viewModel: UserRecipesViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_recipes, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = RecipeAdapter(recipes) { recipe ->
            showCommentsDialog(recipe)
        }
        recyclerView.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        setupObservers()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (lastVisibleItem == recipes.size - 1) {
                    viewModel.loadRecipes()
                }
            }
        })

        viewModel.loadRecipes()

        return view
    }

    private fun setupObservers() {
        viewModel.recipes.observe(viewLifecycleOwner) { newRecipes ->
            recipes.clear()
            recipes.addAll(newRecipes)
            adapter.notifyDataSetChanged()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Optionally show a loading spinner here
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
