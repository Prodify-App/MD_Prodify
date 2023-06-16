package com.c23ps105.prodify.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PredictResponse(
    @field:SerializedName("title")
    val title: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("error")
    val error: String
)

@Parcelize
data class PredictDataModel(
    val title: String,
    val description: String,
    val error: String
) : Parcelable