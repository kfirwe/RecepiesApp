package com.example.finalproject.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.data.repositories.RecipeRepository
import com.example.finalproject.database.AppDatabase
import kotlinx.coroutines.launch

class SearchRecipeViewModel(context: Context) : ViewModel() {

    private val repository: RecipeRepository

    init {
        val recipeDao = AppDatabase.getDatabase(context).recipeDao()
        repository = RecipeRepository(recipeDao)
    }

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun performSearch(query: String, searchBy: String, condition: String, limit: Int) {
        if (query.isBlank()) {
            _errorMessage.value = "Enter a search query"
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = repository.searchRecipes(query, searchBy, condition, limit)
                _recipes.value = result
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
