package com.c23ps105.prodify.data.remote.retrofit

import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.utils.cat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiConfig {
    fun getApiService(sessionPreference: SessionPreferences): ApiService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                val tokenFlow = sessionPreference.getPreference()

                val preference = runBlocking { tokenFlow.first() }
                val requestWithHeader = request.newBuilder()
                    .header("Authorization", "Bearer ${preference.token}")
                    .build()

                val response = chain.proceed(requestWithHeader)
                response
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://capstone-project-prodify-app.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    fun getPredictService(): ApiPredictService {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES)
            .build()

        return Retrofit.Builder()
            .baseUrl("http://34.143.157.187:8080/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiPredictService::class.java)
    }
}
