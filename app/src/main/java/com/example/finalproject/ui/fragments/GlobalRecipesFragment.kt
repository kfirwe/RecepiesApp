package com.example.finalproject.ui.fragments


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.data.models.GlobalRecipe
import com.example.finalproject.ui.adapters.RecipeGlobalAdapter
import com.example.finalproject.viewmodels.GlobalRecipesViewModel

class GlobalRecipesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeGlobalAdapter
    private lateinit var viewModel: GlobalRecipesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_global_recipes, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RecipeGlobalAdapter(emptyList()) { recipe ->
            showRecipeDialog(recipe)
        }
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this).get(GlobalRecipesViewModel::class.java)

        // Observe recipes LiveData
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateRecipes(recipes)
        }

        // Fetch initial recipes
        viewModel.fetchRecipes()

        // Add scroll listener for infinite scrolling
        setupScrollListener()

        return view
    }

    private fun setupScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                    firstVisibleItemPosition >= 0) {
                    // Fetch the next batch of recipes
                    viewModel.fetchRecipes()
                }
            }
        })
    }


    private fun showRecipeDialog(recipe: GlobalRecipe) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_global_recipe)

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),
            (resources.displayMetrics.heightPixels * 0.6).toInt()
        )

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val dialogIngredients = dialog.findViewById<TextView>(R.id.dialogIngredients)
        val dialogClose = dialog.findViewById<Button>(R.id.dialogClose)

        dialogTitle.text = recipe.title
        dialogIngredients.text = "Fetching ingredients..."

        viewModel.fetchIngredients(recipe.id) { ingredientsText ->
            dialogIngredients.text = ingredientsText ?: "Failed to fetch ingredients."
        }

        dialogClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
