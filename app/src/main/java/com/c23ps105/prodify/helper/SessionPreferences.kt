package com.c23ps105.prodify.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.c23ps105.prodify.data.PreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val tokenKey = stringPreferencesKey(TOKEN_KEY)
    private val idUser = intPreferencesKey(ID_USER)
    private val username = stringPreferencesKey(USERNAME)
    private val email = stringPreferencesKey(EMAIL_USER)

    fun getPreference(): Flow<PreferenceKey> {
        return dataStore.data.map {
            PreferenceKey(
                it[idUser] ?: 0,
                it[tokenKey] ?: "",
                it[username] ?: "",
                it[email] ?: ""
            )
        }
    }

    suspend fun deleteSession() {
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun saveSessionSetting(userId: Int, token: String, username: String, email: String) {
        dataStore.edit {
            it[idUser] = userId
            it[tokenKey] = token
            it[this.username] = username
            it[this.email] = email
        }
    }

    companion object {
        private const val TOKEN_KEY = "token_key"
        private const val USERNAME = "username_key"
        private const val ID_USER = "id_user_key"
        private const val EMAIL_USER = "email_user_key"

        @Volatile
        private var INSTANCE: SessionPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SessionPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
