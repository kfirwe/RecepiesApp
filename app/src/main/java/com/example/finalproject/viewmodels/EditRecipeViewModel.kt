package com.example.finalproject.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.repositories.RecipeRepository
import kotlinx.coroutines.launch

class EditRecipeViewModel : ViewModel() {

    private val repository = RecipeRepository()

    fun updateRecipe(recipeId: String, updatedData: Map<String, Any>): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch {
            try {
                repository.updateRecipe(recipeId, updatedData)
                result.value = true
            } catch (e: Exception) {
                result.value = false
            }
        }
        return result
    }
}
