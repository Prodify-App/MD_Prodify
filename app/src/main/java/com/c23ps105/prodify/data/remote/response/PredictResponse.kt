package com.c23ps105.prodify.data.remote.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("description")
    val description: String
)