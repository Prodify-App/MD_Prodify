package com.c23ps105.prodify.data.remote.retrofit

import com.c23ps105.prodify.data.remote.response.LoginTestResponse
import com.c23ps105.prodify.data.remote.response.RegisterTestResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiAuthTestService {
    @FormUrlEncoded
    @POST("/v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterTestResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginTestResponse>

}

