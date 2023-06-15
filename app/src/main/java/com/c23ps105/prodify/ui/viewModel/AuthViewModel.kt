package com.c23ps105.prodify.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.c23ps105.prodify.data.PreferenceKey
import com.c23ps105.prodify.data.repository.AuthRepository
import com.c23ps105.prodify.helper.SessionPreferences
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val pref: SessionPreferences,
) : ViewModel() {

    fun getPreferences(): LiveData<PreferenceKey> {
        return pref.getPreference().asLiveData()
    }

    fun saveSettings(userId: Int, token: String, username: String, email: String) {
        viewModelScope.launch {
            pref.saveSessionSetting(userId, token, username, email)
        }
    }

    fun deleteSettings() {
        viewModelScope.launch {
            pref.deleteSession()
        }
    }

    fun login(email: String, password: String) = authRepository.getLoginResult(email, password)

    fun register(username: String, email: String, password: String) =
        authRepository.getRegisterResult(username, email, password)

    fun getRegisterResult() = authRepository.isRegistered

    fun getToastText() = authRepository.toastText
    fun setToastText(text: String) = authRepository.setToastText(text)
}