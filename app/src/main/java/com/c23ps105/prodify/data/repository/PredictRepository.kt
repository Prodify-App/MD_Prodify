package com.c23ps105.prodify.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.c23ps105.prodify.data.remote.response.PredictResponse
import com.c23ps105.prodify.data.remote.retrofit.ApiPredictService
import com.c23ps105.prodify.utils.Event
import com.c23ps105.prodify.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PredictRepository(
    private val api: ApiPredictService
) {
    private val predictResult = MediatorLiveData<Result<List<String>>>()

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    fun predict(category: RequestBody, image: MultipartBody.Part): LiveData<Result<List<String>>> {
        predictResult.value = Result.Loading

        val client = api.predict(image, category)
        client.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(
                call: Call<PredictResponse>,
                response: Response<PredictResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    predictResult.value = Result.Success(
                        listOf(
                            body?.title.toString(),
                            body?.description.toString()
                        )
                    )
                } else {
                    predictResult.value = Result.Error("ups ! response unSuccessful")
                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                predictResult.value = Result.Error("ups ! Network Failure : ${t.message}")
            }
        })
        return predictResult
    }

    fun setToastText(text: String) {
        _toastText.value = Event(text)
    }


    companion object {
        @Volatile
        private var instance: PredictRepository? = null

        fun getInstance(
            apiService: ApiPredictService
        ): PredictRepository =
            instance ?: synchronized(this) {
                instance ?: PredictRepository(apiService)
            }.also { instance = it }
    }
}