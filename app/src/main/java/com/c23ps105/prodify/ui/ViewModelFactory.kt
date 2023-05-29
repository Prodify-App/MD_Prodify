package com.c23ps105.prodify.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c23ps105.prodify.data.SessionPreferences
import com.c23ps105.prodify.data.StoryRepository
import com.c23ps105.prodify.di.Injection


class ViewModelFactory(
    private val storyRepository: StoryRepository,
    private val pref: SessionPreferences
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(StoryViewModel::class.java)) {
//            return StoryViewModel(storyRepository, pref) as T
//        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context, pref: SessionPreferences): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context), pref)
            }.also { instance = it }
    }
}