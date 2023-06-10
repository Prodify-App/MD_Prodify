package com.c23ps105.prodify.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val pref: SessionPreferences
) : ViewModel() {
    fun getSessionSettings(): LiveData<Boolean> {
        return pref.getSession().asLiveData()
    }
    fun getTokenSetting(): LiveData<String> {
        return pref.getToken().asLiveData()
    }
    fun saveSettings(token: String) {
        viewModelScope.launch {
            pref.saveSessionSetting(token, token.isNotEmpty())
        }
    }

    fun deleteSettings() {
        viewModelScope.launch {
            pref.deleteSession()
        }
    }

    fun login(email: String, password: String) = authRepository.getLoginResult(email, password)

    fun register(username: String,email: String, password: String) = authRepository.getRegisterResult(username, email, password)
    fun getRegisterResult() = authRepository.isRegistered

    fun getToastText() = authRepository.toastText
    fun setToastText(text : String) = authRepository.setToastText(text)
}