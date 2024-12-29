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

    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments

    private val _userIdToNameMap = MutableLiveData<Map<String, String>>()
    val userIdToNameMap: LiveData<Map<String, String>> get() = _userIdToNameMap

    private val _isEditing = MutableLiveData<Comment?>()
    val isEditing: LiveData<Comment?> get() = _isEditing

    fun fetchComments(recipeId: String) {
        viewModelScope.launch {
            try {
                val fetchedComments = repository.fetchComments(recipeId)
                _comments.postValue(fetchedComments)
                fetchUserNames(fetchedComments)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchUserNames(comments: List<Comment>) {
        val userIds = comments.mapNotNull { it.userId }.distinct()
        val userIdToName = mutableMapOf<String, String>()

        try {
            for (userId in userIds) {
                val userName = repository.fetchUserNameById(userId)
                userName?.let { userIdToName[userId] = it }
            }
            _userIdToNameMap.postValue(userIdToName)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addComment(recipeId: String, comment: Comment) {
        viewModelScope.launch {
            try {
                repository.addComment(recipeId, comment)
                fetchComments(recipeId) // Refresh comments
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateComment(recipeId: String, commentId: String, updatedData: Map<String, Any>) {
        viewModelScope.launch {
            try {
                repository.updateComment(recipeId, commentId, updatedData)
                fetchComments(recipeId) // Refresh comments
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteComment(recipeId: String, commentId: String) {
        viewModelScope.launch {
            try {
                repository.deleteComment(recipeId, commentId)
                fetchComments(recipeId) // Refresh comments
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setEditingComment(comment: Comment?) {
        _isEditing.value = comment
    }
}

