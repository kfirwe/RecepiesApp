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
import android.app.AlertDialog
import android.app.Dialog
import android.widget.Button
import android.widget.TextView


class GlobalRecipesFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val recipes = mutableListOf<Recipe>()
    private var offset = 0
    private val pageSize = 10
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_global_recipes, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = RecipeAdapter(recipes) { recipe ->
            showRecipeDialog(recipe) // Handle recipe click
        }
        recyclerView.adapter = adapter

        fetchRecipesFromAPI()

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && lastVisibleItem == recipes.size - 1) {
                    fetchRecipesFromAPI()
                }
            }
        })

        return view
    }

    private fun showRecipeDialog(recipe: Recipe) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_global_recipe)

        // Set dialog dimensions to make it more square-like
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9).toInt(),  // 90% of screen width
            (resources.displayMetrics.heightPixels * 0.6).toInt() // 60% of screen height
        )

        val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
        val dialogIngredients = dialog.findViewById<TextView>(R.id.dialogIngredients)
        val dialogClose = dialog.findViewById<Button>(R.id.dialogClose)

        dialogTitle.text = recipe.title
        dialogIngredients.text = "Fetching ingredients..."

        // Fetch ingredients
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getIngredients(
                    recipeId = recipe.id,
                    apiKey = "018e269aad9f4e319ae2e37e383ec814"
                )
                withContext(Dispatchers.Main) {
                    val ingredientsText = response.ingredients.joinToString("\n") {
                        "${it.amount.metric.value} ${it.amount.metric.unit} - ${it.name}"
                    }
                    dialogIngredients.text = ingredientsText
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    dialogIngredients.text = "Failed to fetch ingredients."
                }
            }
        }

        dialogClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }



    private fun fetchRecipesFromAPI() {
        isLoading = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getRecipes(
                    query = "",
                    number = pageSize,
                    offset = offset,
                    apiKey = "018e269aad9f4e319ae2e37e383ec814"
                )

                if (offset >= response.totalResults) {
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    response.results?.let {
                        recipes.addAll(it)
                        adapter.notifyDataSetChanged()
                        offset += pageSize
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
