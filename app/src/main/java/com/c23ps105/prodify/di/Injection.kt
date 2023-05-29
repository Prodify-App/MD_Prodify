package com.c23ps105.prodify.di

import android.content.Context
import com.c23ps105.prodify.data.local.room.StoryDatabase
import com.c23ps105.prodify.data.StoryRepository
import com.c23ps105.prodify.data.remote.retrofit.ApiConfig
import com.c23ps105.prodify.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()
        val appExecutors = AppExecutors()
        return StoryRepository.getInstance(apiService, dao, appExecutors)
    }
}