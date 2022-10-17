package com.auth.app.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth.app.data.RegisterBody
import com.auth.app.data.User
import com.auth.app.data.ValidateEmailBody
import com.auth.app.repository.AuthRepository
import com.auth.app.utils.AuthToken
import com.auth.app.utils.RequestStatus
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class RegisterActivityViewModel(val authRepository: AuthRepository, val application: Application) :
    ViewModel() {

    private var isLoading: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    private var errorMessage: MutableLiveData<HashMap<String, String>> = MutableLiveData()
    private var isUnique: MutableLiveData<Boolean> =
        MutableLiveData<Boolean>().apply { value = false }
    private var user: MutableLiveData<User> = MutableLiveData()

    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getErrorMessage(): LiveData<HashMap<String, String>> = errorMessage
    fun getIsUnique(): LiveData<Boolean> = isUnique
    fun getUser(): LiveData<User> = user

    fun validateEmailAddress(body: ValidateEmailBody) {
        viewModelScope.launch {
            authRepository.validateEmailAddress(body).collect {
                when (it) {
                    is RequestStatus.Waiting -> {
                        isLoading.value = true
                    }
                    is RequestStatus.Success -> {
                        isLoading.value = false
                        isUnique.value = it.data.isUnique
                    }
                    is RequestStatus.Error -> {
                        isLoading.value = false
                        errorMessage.value = it.message
                    }
                }
            }
        }
    }

    fun registerUser(body: RegisterBody) {
        viewModelScope.launch {
            authRepository.registerUser(body).collect {
                when (it) {
                    is RequestStatus.Waiting -> {
                        isLoading.value = true
                    }
                    is RequestStatus.Success -> {
                        isLoading.value = false
                        user.value = it.data.user
                        AuthToken.getInstance(application.baseContext).token = it.data.token
                        Log.d("stuff", "${it.data.user}")
                    }
                    is RequestStatus.Error -> {
                        isLoading.value = false
                        errorMessage.value = it.message
                    }
                }
            }
        }
    }
}