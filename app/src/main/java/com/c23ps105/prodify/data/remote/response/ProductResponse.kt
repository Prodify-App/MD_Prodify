package com.c23ps105.prodify.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @field:SerializedName("Response")
    val productList: List<DetailProductResponse>
)

data class DetailProductResponse(
    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("imageURL")
    val imageURL: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("category")
    val category: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class UploadProductResponse(
    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("imageURL")
    val imageURL: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("category")
    val category: String,
)