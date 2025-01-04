package com.example.finalproject.viewmodels


import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.repositories.AuthRepository
import com.example.finalproject.data.repositories.UserRepository
import com.example.finalproject.database.dao.UserDao
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository, private val userDao: UserDao) : ViewModel() {

    private val userRepository = UserRepository(userDao)

    private val _signUpStatus = MutableLiveData<Boolean>()
    val signUpStatus: LiveData<Boolean> get() = _signUpStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun signUp(email: String, password: String, name: String, context: Context) {
        viewModelScope.launch {
            try {
                val userId = authRepository.signUp(email, password)
                userRepository.saveUser(userId, name, email)
                userRepository.uploadDefaultProfilePicture(userId, context)
                _signUpStatus.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
            }
        }
    }

//    fun setContext(context: Context) {
//        authRepository = AuthRepository(context)
//        userRepository = UserRepository(context)
//    }


    fun clearError() {
        _errorMessage.value = null
    }
}
