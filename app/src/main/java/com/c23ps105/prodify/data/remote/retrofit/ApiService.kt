package com.c23ps105.prodify.data.remote.retrofit

import com.c23ps105.prodify.data.remote.response.DetailResponse
import com.c23ps105.prodify.data.remote.response.ListStoryResponse
import com.c23ps105.prodify.data.remote.response.LoginResponse
import com.c23ps105.prodify.data.remote.response.MainResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("/v1/stories")
    fun uploadImage(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
    ): Call<MainResponse>

    @FormUrlEncoded
    @POST("/v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<MainResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("/v1/stories")
    fun getStories(
        @Header("Authorization") authorization: String,
    ): Call<ListStoryResponse>

    @GET("v1/stories/{id}")
    fun getDetailStories(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): Call<DetailResponse>

}

