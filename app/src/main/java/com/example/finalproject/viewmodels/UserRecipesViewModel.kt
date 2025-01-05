package com.example.finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.models.Recipe
import com.example.finalproject.data.repositories.RecipeRepository
import kotlinx.coroutines.launch

class UserRecipesViewModel(private val repository: RecipeRepository) : ViewModel() {

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    private val currentRecipes = mutableListOf<Recipe>()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val pageSize = 10

    fun loadRecipes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val fetchedRecipes = repository.fetchAndStoreRecipes(pageSize) // Fetch from Firestore
                if (fetchedRecipes.isNotEmpty()) {
                    currentRecipes.addAll(fetchedRecipes) // Append to the existing list
                    _recipes.value = currentRecipes // Use Firestore data
                } else {
                    // Fallback to Room (no images)
                    val roomRecipes = repository.getRecipesFromRoom().value ?: emptyList()
                    _recipes.value = roomRecipes.map { Recipe.fromEntity(it) }
                }
            } catch (e: Exception) {
                _errorMessage.postValue(e.message)
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}


