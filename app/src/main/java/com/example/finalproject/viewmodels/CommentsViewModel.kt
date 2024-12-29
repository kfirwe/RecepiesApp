package com.example.finalproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.models.Comment
import com.example.finalproject.data.repositories.CommentsRepository
import com.example.finalproject.data.repositories.UserRepository
import kotlinx.coroutines.launch

class CommentsViewModel : ViewModel() {

    private val repository = CommentsRepository()
    private val userRepository = UserRepository() // Add UserRepository for fetching user data

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments

    private val _isEditing = MutableLiveData<Comment?>()
    val isEditing: LiveData<Comment?> get() = _isEditing

    fun fetchComments(recipeId: String) {
        viewModelScope.launch {
            val fetchedComments = repository.fetchComments(recipeId)
            _comments.postValue(fetchedComments)
        }
    }

    fun addComment(recipeId: String, comment: Comment) {
        viewModelScope.launch {
            try {
                repository.addComment(recipeId, comment)
                fetchComments(recipeId) // Refresh comments
            } catch (e: Exception) {
                e.printStackTrace() // Handle error
            }
        }
    }

    fun updateComment(recipeId: String, commentId: String, updatedData: Map<String, Any>) {
        viewModelScope.launch {
            try {
                repository.updateComment(recipeId, commentId, updatedData)
                fetchComments(recipeId) // Refresh comments
            } catch (e: Exception) {
                e.printStackTrace() // Handle error
            }
        }
    }

    fun deleteComment(recipeId: String, commentId: String) {
        viewModelScope.launch {
            try {
                repository.deleteComment(recipeId, commentId)
                fetchComments(recipeId) // Refresh comments
            } catch (e: Exception) {
                e.printStackTrace() // Handle error
            }
        }
    }

    fun setEditingComment(comment: Comment?) {
        _isEditing.value = comment
    }

    // Add the getUserName function
    fun getUserName(userId: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val userProfile = userRepository.getProfile() // Fetch user profile
                callback(userProfile?.displayName) // Pass the display name to the callback
            } catch (e: Exception) {
                callback(null) // Pass null if an error occurs
            }
        }
    }
}
