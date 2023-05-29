package com.c23ps105.prodify.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.c23ps105.prodify.data.local.entity.StoryEntity
import com.c23ps105.prodify.data.local.room.StoryDao
import com.c23ps105.prodify.data.remote.response.ListStoryResponse
import com.c23ps105.prodify.data.remote.response.LoginResponse
import com.c23ps105.prodify.data.remote.retrofit.ApiConfig
import com.c23ps105.prodify.data.remote.retrofit.ApiService
import com.c23ps105.prodify.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StoryRepository private constructor(
    private val api: ApiService,
    private val storyDao: StoryDao,
    private val appExecutors: AppExecutors
) {
    private val storyResult = MediatorLiveData<Result<List<StoryEntity>>>()
    private val detailResult = MediatorLiveData<Result<List<StoryEntity>>>()
    private val tokenResult = MediatorLiveData<Result<List<String>>>()

    fun getStoriesList(token: String?): LiveData<Result<List<StoryEntity>>> {
        storyResult.value = Result.Loading
        Log.d(TAG, token.toString())
        val client = api.getStories("Bearer $token")
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val stories = response.body()?.listStory

                    val storyList = ArrayList<StoryEntity>()
                    appExecutors.diskIO.execute {
                        stories?.forEach { story ->
                            val news = StoryEntity(
                                story.id,
                                story.createdAt,
                                story.photoUrl,
                                story.name,
                                story.description,
                                story.lon as Double?,
                                story.lat as Double?,
                            )
                            storyList.add(news)
                        }
                        storyDao.deleteAll()

                        storyDao.insertStory(storyList)
                    }
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                storyResult.value = Result.Error(t.message.toString())
            }
        })

        val localData = storyDao.getStories()
        storyResult.addSource(localData) { newData: List<StoryEntity> ->
            storyResult.value = Result.Success(newData)
        }
        return storyResult
    }

    fun getLoginResult(email: String, password: String): LiveData<Result<List<String>>> {
        tokenResult.value = Result.Loading

        val apiService = ApiConfig.getApiService()
        val uploadRegister = apiService.login(email, password)
        uploadRegister.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        Log.d(TAG, response.body().toString())
                        val token = responseBody.loginResult.token
                        tokenResult.value = Result.Success(listOf(token))
                    } else {
                        Log.d(TAG, response.message())
                    }
                } else {
                    tokenResult.value = Result.Error(response.body()?.message ?: response.message())
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                tokenResult.value = Result.Error(t.message.toString())
            }
        })
        return tokenResult
    }

    fun getDetailStories(id: String): LiveData<Result<List<StoryEntity>>> {
        detailResult.value = Result.Loading

        val localData = storyDao.getDetailStories(id)
        detailResult.addSource(localData) { newData: List<StoryEntity> ->
            detailResult.value = Result.Success(newData)
        }
        return detailResult
    }

    fun getCameraResult(){

    }
    companion object {
        private val TAG = StoryRepository::class.java.simpleName

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            api: ApiService,
            storyDao: StoryDao,
            appExecutors: AppExecutors
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(api, storyDao, appExecutors)
            }.also { instance = it }
    }
}
