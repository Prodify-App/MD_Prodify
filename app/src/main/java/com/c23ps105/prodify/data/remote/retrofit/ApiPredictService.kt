package com.c23ps105.prodify.data.remote.retrofit

import com.c23ps105.prodify.data.remote.response.PredictResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiPredictService {
    @Multipart
    @POST("/")
    fun predict(
        @Part image: MultipartBody.Part,
        @Part("category") category: RequestBody,
    ): Call<PredictResponse>
}