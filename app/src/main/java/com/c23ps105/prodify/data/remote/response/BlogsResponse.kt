package com.c23ps105.prodify.data.remote.response

import com.google.gson.annotations.SerializedName


data class BlogsResponse(
    @field:SerializedName("Status")
    val status: String,

    @field:SerializedName("Message")
    val message: String,

    @field:SerializedName("Articles")
    val articles: List<BlogsItem>,
)

data class BlogsItem(
    @field:SerializedName("id")
    val blogId: Int,

    @field:SerializedName("imageURL")
    val imageURL: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("title")
    val title: String? = null,
)