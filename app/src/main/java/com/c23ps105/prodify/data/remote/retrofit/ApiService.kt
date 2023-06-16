package com.c23ps105.prodify.data.remote.retrofit

import com.c23ps105.prodify.data.remote.response.BlogsItem
import com.c23ps105.prodify.data.remote.response.BlogsResponse
import com.c23ps105.prodify.data.remote.response.LoginResponse
import com.c23ps105.prodify.data.remote.response.ProductResponse
import com.c23ps105.prodify.data.remote.response.ProductsItem
import com.c23ps105.prodify.data.remote.response.RegisterResponse
import com.c23ps105.prodify.data.remote.response.UploadProductResponse
import com.c23ps105.prodify.data.remote.response.UserProductResponse
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
        @Field("password") password: String,
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @Multipart
    @POST("insertProducts")
    fun uploadProduct(
        @Part attachment: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody,
        @Part("id_user") idUser: RequestBody,
    ): Call<UploadProductResponse>

    @GET("getProducts")
    fun getAllProduct(): Call<ProductResponse>

    @GET("getProducts/user/{id}")
    fun getAllUserProduct(
        @Path("id") userId: Int,
    ): Call<UserProductResponse>


    @GET("getProducts/product/{id}")
    fun getDetailProduct(
        @Path("id") id: Int,
    ): Call<ProductsItem>


    @GET("getArticles")
    fun getBlogs(): Call<BlogsResponse>

    @GET("getArticles/articles/{id}")
    fun getBlogsById(
        @Path("id") ProductId: Int,
    ): Call<BlogsItem>

}

