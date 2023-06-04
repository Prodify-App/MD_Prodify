package com.c23ps105.prodify.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.c23ps105.prodify.data.remote.response.LoginTestResponse
import com.c23ps105.prodify.data.remote.response.RegisterTestResponse
import com.c23ps105.prodify.data.remote.retrofit.ApiAuthTestService
import com.c23ps105.prodify.utils.Event
import com.c23ps105.prodify.utils.Result
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository private constructor(
    private val apiService: ApiAuthTestService
) {
    private val tokenResult = MediatorLiveData<Result<List<String>>>()

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText

    private val _isRegistered = MutableLiveData<Boolean?>()
    val isRegistered: LiveData<Boolean?> = _isRegistered

    fun getLoginResult(email: String, password: String): LiveData<Result<List<String>>> {
        tokenResult.value = Result.Loading

        val login = apiService.login(email, password)
        login.enqueue(object : Callback<LoginTestResponse> {
            override fun onResponse(
                call: Call<LoginTestResponse>,
                response: Response<LoginTestResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        val result = responseBody.loginResult
                        tokenResult.value =
                            Result.Success(
                                listOf(
                                    result.token,
                                    result.name,
                                    result.userId,
                                    responseBody.message
                                )
                            )
                    } else {
                        _toastText.value =
                            Event(responseBody?.message ?: "Ups ! something is wrong.")
                    }
                } else {
                    val errorBody = JSONObject(response.errorBody()!!.string()).getString("message")
                    tokenResult.value = Result.Error(errorBody)
                }
            }

            override fun onFailure(call: Call<LoginTestResponse>, t: Throwable) {
                tokenResult.value = Result.Error("Failure : " + t.message)
            }
        })
        return tokenResult
    }

    fun getRegisterResult(username: String, email: String, password: String) {
        _isRegistered.value = null

        val register = apiService.register(username, email, password)
        register.enqueue(object : Callback<RegisterTestResponse> {
            override fun onResponse(
                call: Call<RegisterTestResponse>,
                response: Response<RegisterTestResponse>
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
                    val errorBody = JSONObject(response.errorBody()!!.string())
                    _toastText.value = Event(errorBody.getString("message"))
                }
            }

            override fun onFailure(call: Call<RegisterTestResponse>, t: Throwable) {
                _isRegistered.value = false
                _toastText.value = Event(t.message.toString())
            }
        })
    }


    fun setToastText(text: String){
        _toastText.value = Event(text)
    }

    companion object {
        private var TAG = AuthRepository::class.java.simpleName

        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            apiService: ApiAuthTestService
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService)
            }.also { instance = it }
    }
}
