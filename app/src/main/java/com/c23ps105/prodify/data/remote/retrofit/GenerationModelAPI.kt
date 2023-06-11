package com.c23ps105.prodify.data.remote.retrofit

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

interface GenerationModelAPI {
    @Multipart
    @POST("/")
    fun generateText(
        @Part image: MultipartBody.Part,
        @Part("category") category: RequestBody,
    ): Call<GeneratedTextResponse>

//    Call<GeneratedTextResponse>

    companion object {
        operator fun invoke(): GenerationModelAPI {

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build()

            return Retrofit.Builder()
                .baseUrl("http://34.126.100.174:8080/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GenerationModelAPI::class.java)
        }
    }
}

data class GeneratedTextResponse(
    val description: String,
    val title: String
)