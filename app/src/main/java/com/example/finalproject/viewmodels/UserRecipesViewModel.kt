package com.example.finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.data.repositories.RecipeRepository
import kotlinx.coroutines.launch

class UserRecipesViewModel : ViewModel() {

    private val repository = RecipeRepository()

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val pageSize = 10

    fun loadRecipes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val newRecipes = repository.fetchAllRecipes(pageSize)
                val currentRecipes = _recipes.value.orEmpty()
                _recipes.value = currentRecipes + newRecipes
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
