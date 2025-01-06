package com.example.finalproject.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalproject.api.RetrofitClient
import com.example.finalproject.data.models.GlobalRecipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GlobalRecipesViewModel : ViewModel() {

    private val _recipes = MutableLiveData<List<GlobalRecipe>>()
    val recipes: LiveData<List<GlobalRecipe>> get() = _recipes

    private var offset = 0
    private val pageSize = 10
    private var isLoading = false

    fun fetchRecipes() {
        if (isLoading) return
        isLoading = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                println("Fetching recipes... Offset: $offset, PageSize: $pageSize")

                val response = RetrofitClient.apiService.getRecipes(
                    query = "",
                    number = pageSize,
                    offset = offset,
                    apiKey = "3dface284ea44e9f96584b222191ebb2"
                )

                println("API Response: $response")
                if (response.results.isNullOrEmpty()) {
                    println("No recipes found in this batch.")
                }

                if (offset >= response.totalResults) {
                    println("Reached the end of available recipes. Total: ${response.totalResults}")
                    return@launch
                }

                val newRecipes = response.results?.map { recipe ->
                    println("Processing Recipe: ${recipe.title}")
                    GlobalRecipe(
                        id = recipe.id,
                        title = recipe.title,
                        userId = recipe.userId,
                        description = recipe.description ?: "",
                        imageUrl = recipe.imageUrl ?: ""
                    )
                } ?: emptyList()

                withContext(Dispatchers.Main) {
                    println("Fetched ${newRecipes.size} recipes.")
                    _recipes.value = (_recipes.value ?: emptyList()) + newRecipes
                    offset += pageSize
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error fetching recipes: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }


    fun fetchIngredients(recipeId: Int, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                println("Fetching ingredients for Recipe ID: $recipeId")
                val response = RetrofitClient.apiService.getIngredients(
                    recipeId = recipeId,
                    apiKey = "3dface284ea44e9f96584b222191ebb2"
                )
                println("Fetched Ingredients: $response")
                val ingredientsText = response.ingredients.joinToString("\n") {
                    "${it.amount.metric.value} ${it.amount.metric.unit} - ${it.name}"
                }
                withContext(Dispatchers.Main) {
                    callback(ingredientsText)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error fetching ingredients: ${e.message}")
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }

}
