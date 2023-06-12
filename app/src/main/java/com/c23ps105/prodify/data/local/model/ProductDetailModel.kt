package com.c23ps105.prodify.data.local.model

data class ProductDetailModel(
    val id: Int,
    val createdAt: String,
    val updatedAt: String,
    val title: String,
    val category: String,
    val description: String,
    val imageURL: String,
    val isBookmarked : Boolean
)