package com.c23ps105.prodify.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @field:SerializedName("message")
    val message: String
)

data class LoginResponse(

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("accessToken")
    val accessToken: String,

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("username")
    val username: String
)