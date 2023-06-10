package com.c23ps105.prodify.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SessionPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val SESSION_KEY = booleanPreferencesKey("session_key")
    private val TOKEN_KEY = stringPreferencesKey("token_key")

    fun getSession(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[SESSION_KEY] ?: false
        }
    }

    fun getToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    suspend fun deleteSession() {
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun saveSessionSetting(token: String, isSessionActive: Boolean) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[SESSION_KEY] = isSessionActive
        }
    }

    companion object {
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
