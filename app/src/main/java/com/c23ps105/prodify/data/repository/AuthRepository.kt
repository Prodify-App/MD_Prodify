package com.c23ps105.prodify.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.c23ps105.prodify.data.remote.response.LoginResponse
import com.c23ps105.prodify.data.remote.response.RegisterResponse
import com.c23ps105.prodify.data.remote.retrofit.ApiService
import com.c23ps105.prodify.utils.Event
import com.c23ps105.prodify.utils.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository private constructor(
    private val apiService: ApiService,
) {
    private val tokenResult = MediatorLiveData<Result<LoginResponse>>()

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    private val _isRegistered = MutableLiveData<Boolean?>()
    val isRegistered: LiveData<Boolean?> = _isRegistered

    fun getLoginResult(email: String, password: String): LiveData<Result<LoginResponse>> {
        tokenResult.value = Result.Loading

        val login = apiService.login(email, password)
        login.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>,
            ) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        tokenResult.value =
                            Result.Success(
                                LoginResponse(
                                    result.id,
                                    result.accessToken,
                                    result.email,
                                    result.username
                                )
                            )
                    } else {
                        _toastText.value =
                            Event("Ups ! something is wrong.")
                    }
                } else {
//                    val errorBody = JSONObject(response.errorBody()!!.string()).getString("message")
//                    tokenResult.value = Result.Error(errorBody)
                    tokenResult.value = Result.Error("Ups ! Response Failed")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                tokenResult.value = Result.Error("Failure : " + t.message)
            }
        })
        return tokenResult
    }

    fun getRegisterResult(username: String, email: String, password: String) {
        _isRegistered.value = null

        val register = apiService.register(username, email, password)
        Log.d(TAG, listOf(username, email, password).toString())
        register.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>,
            ) {
                val responseBody = response.body()
                if (response.isSuccessful) {
                    if (responseBody != null) {
                        _isRegistered.value = true
                        _toastText.value = Event(responseBody.message)
                    } else {
                        _toastText.value = Event("Terjadi Kesalahan")
                    }
                } else {
                    _isRegistered.value = false
                    Log.d(TAG, response.raw().toString())
                    _toastText.value = Event("Harap mengisi data yang benar")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isRegistered.value = false
                _toastText.value = Event(t.message.toString())
            }
        })
    }


    fun setToastText(text: String) {
        _toastText.value = Event(text)
    }

    companion object {
        private var TAG = AuthRepository::class.java.simpleName

        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiService,
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }
}
