package com.c23ps105.prodify.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.c23ps105.prodify.R
import com.c23ps105.prodify.helper.AuthViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.ui.main.MainActivity
import com.c23ps105.prodify.ui.viewModel.AuthViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val pref = SessionPreferences.getInstance(dataStore)
        val factory = AuthViewModelFactory.getInstance(pref)
        val viewModel: AuthViewModel by viewModels { factory }
        viewModel.getPreferences().observe(this) { key ->
            if (key.token.isNotEmpty()) {
                Intent(this, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            } else {
                setContentView(R.layout.activity_auth)
            }
        }
    }
}