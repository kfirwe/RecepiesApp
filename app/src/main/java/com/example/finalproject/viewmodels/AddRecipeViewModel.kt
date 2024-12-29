package com.example.finalproject.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.repositories.RecipeRepository
import kotlinx.coroutines.launch

class AddRecipeViewModel : ViewModel() {

    private val repository = RecipeRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _uploadSuccess = MutableLiveData<Boolean>()
    val uploadSuccess: LiveData<Boolean> get() = _uploadSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun uploadRecipe(title: String, description: String, imageBase64: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                repository.addRecipe(title, description, imageBase64)
                _uploadSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
