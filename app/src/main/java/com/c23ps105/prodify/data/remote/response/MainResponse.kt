package com.c23ps105.prodify.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @field:SerializedName("Status")
    val status: String,

    @field:SerializedName("Message")
    val message: String,

    @field:SerializedName("listProducts")
    val listProducts: List<ProductsItem>,
)

data class ProductsItem(

    @field:SerializedName("id")
    val idProduct: Int,

    @field:SerializedName("id_user")
    val idUser: Int,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("category")
    val category: String,

    @field:SerializedName("imageURL")
    val imageURL: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null,
)

data class UploadProductResponse(
    @field:SerializedName("id_user")
    val idUser: Int,

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
