package com.c23ps105.prodify.di

import android.content.Context
import com.c23ps105.prodify.data.repository.ProductRepository
import com.c23ps105.prodify.data.local.room.ProductDatabase
import com.c23ps105.prodify.data.remote.retrofit.ApiConfig
import com.c23ps105.prodify.data.repository.AuthRepository
import com.c23ps105.prodify.utils.AppExecutors

object Injection {
    fun provideProductRepository(context: Context): ProductRepository {
        val apiService = ApiConfig.getApiService()
        val database = ProductDatabase.getInstance(context)
        val dao = database.productDao()
        val appExecutors = AppExecutors()
        return ProductRepository.getInstance(apiService, dao, appExecutors)
    }

    fun provideAuthRepository(): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService)
    }
}