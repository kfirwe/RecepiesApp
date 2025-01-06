package com.example.finalproject.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.repositories.RecipeRepository
import com.example.finalproject.database.AppDatabase
import kotlinx.coroutines.launch

class EditRecipeViewModel(context: Context) : ViewModel() {

    private val repository: RecipeRepository

    init {
        val recipeDao = AppDatabase.getDatabase(context).recipeDao()
        repository = RecipeRepository(recipeDao)
    }

    fun updateRecipe(recipeId: String, updatedData: Map<String, Any>): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                repository.updateRecipe(recipeId, updatedData)
                result.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                result.value = false
            }
        }
        return result
    }
}
