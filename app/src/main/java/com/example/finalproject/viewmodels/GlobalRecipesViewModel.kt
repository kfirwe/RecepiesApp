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
                val response = RetrofitClient.apiService.getRecipes(
                    query = "",
                    number = pageSize,
                    offset = offset,
                    apiKey = "258d00a753374df19678d9210db10b17"
                )

                if (offset >= response.totalResults) {
                    return@launch
                }

                val newRecipes = response.results?.map { recipe ->
                    GlobalRecipe(
                        id = recipe.id,
                        title = recipe.title,
                        userId = recipe.userId,
                        description = recipe.description ?: "",
                        imageUrl = recipe.imageUrl ?: ""
                    )
                } ?: emptyList()

                withContext(Dispatchers.Main) {
                    _recipes.value = (_recipes.value ?: emptyList()) + newRecipes
                    offset += pageSize
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchIngredients(recipeId: Int, callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getIngredients(
                    recipeId = recipeId,
                    apiKey = "258d00a753374df19678d9210db10b17"
                )
                val ingredientsText = response.ingredients.joinToString("\n") {
                    "${it.amount.metric.value} ${it.amount.metric.unit} - ${it.name}"
                }
                withContext(Dispatchers.Main) {
                    callback(ingredientsText)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }
}
