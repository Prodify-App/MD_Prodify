package com.c23ps105.prodify.data.remote.retrofit

import com.c23ps105.prodify.data.remote.response.DetailProductResponse
import com.c23ps105.prodify.data.remote.response.LoginResponse
import com.c23ps105.prodify.data.remote.response.RegisterResponse
import com.c23ps105.prodify.data.remote.response.UploadProductResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("username") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("insertProducts")
    fun uploadProduct(
        @Part file: MultipartBody.Part,
        @Part("title") description: RequestBody,
        @Part("category") lat: RequestBody,
        @Part("description") lon: RequestBody,
    ): Call<UploadProductResponse>

    @GET("getProducts")
    fun getProducts(): Call<List<DetailProductResponse>>

    @GET("getProducts/{id}")
    fun getDetailStories(
        @Path("id") id: String
    ): Call<DetailProductResponse>
}

