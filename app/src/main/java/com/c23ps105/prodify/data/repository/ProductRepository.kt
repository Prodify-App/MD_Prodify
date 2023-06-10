package com.c23ps105.prodify.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.c23ps105.prodify.data.local.entity.ProductEntity
import com.c23ps105.prodify.data.local.room.ProductDao
import com.c23ps105.prodify.data.remote.response.DetailProductResponse
import com.c23ps105.prodify.data.remote.response.PredictResponse
import com.c23ps105.prodify.data.remote.response.UploadProductResponse
import com.c23ps105.prodify.data.remote.retrofit.ApiService
import com.c23ps105.prodify.utils.AppExecutors
import com.c23ps105.prodify.utils.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductRepository private constructor(
    private val api: ApiService,
    private val productDao: ProductDao,
    private val appExecutors: AppExecutors
) {

    private val productResult = MediatorLiveData<Result<List<ProductEntity>>>()
    private val detailResult = MediatorLiveData<Result<List<ProductEntity>>>()
    private val predictResult = MediatorLiveData<Result<List<String>>>()
    private val postProductResult = MutableLiveData<String>()
    fun getProductFromApi(token: String?): LiveData<Result<List<ProductEntity>>> {
        productResult.value = Result.Loading
        Log.d(TAG, token.toString())
//        val client = api.getStories("Bearer $token")
        val client = api.getProducts()
        client.enqueue(object : Callback<List<DetailProductResponse>> {
            override fun onResponse(
                call: Call<List<DetailProductResponse>>,
                response: Response<List<DetailProductResponse>>
            ) {
                if (response.isSuccessful) {
                    val detailProductResponseList = response.body()
                    val productList = ArrayList<ProductEntity>()
                    appExecutors.diskIO.execute {
                        detailProductResponseList?.forEach { detailProductResponse ->
                            val isBookmarked =
                                productDao.isProductBookmarked(detailProductResponse.title)
                            val product = ProductEntity(
                                detailProductResponse.id,
                                detailProductResponse.createdAt,
                                detailProductResponse.updatedAt.toString(),
                                detailProductResponse.title,
                                detailProductResponse.category,
                                detailProductResponse.description,
                                detailProductResponse.imageURL,
                                isBookmarked
                            )
                            productList.add(product)
                        }
                        productDao.deleteAll()
                        productDao.insertProduct(productList)
                    }
                }
            }

            override fun onFailure(call: Call<List<DetailProductResponse>>, t: Throwable) {
                Log.d(TAG, t.toString())
                productResult.value = Result.Error(t.message.toString())
            }
        })
        val localData = productDao.getProducts()
        productResult.addSource(localData) { newData: List<ProductEntity> ->
            productResult.value = Result.Success(newData)
        }
        return productResult
    }

    fun getProductList(): LiveData<Result<List<ProductEntity>>> {
        return productResult
    }

    fun getDetailProduct(id: String): LiveData<Result<List<ProductEntity>>> {
        detailResult.value = Result.Loading

        val localData = productDao.getProductById(id)
        detailResult.addSource(localData) { newData: List<ProductEntity> ->
            detailResult.value = Result.Success(newData)
        }
        return detailResult
    }

    fun postProduct(
        image: MultipartBody.Part,
        title: RequestBody,
        category: RequestBody,
        description: RequestBody,
    ): LiveData<String> {
        val client = api.uploadProduct(image, title, category, description)
        Log.d(TAG, client.toString())
        client.enqueue(object : Callback<UploadProductResponse> {
            override fun onResponse(
                call: Call<UploadProductResponse>,
                response: Response<UploadProductResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d(TAG, "Success !")
                    Log.d(TAG, response.body().toString())
                } else {
                    Log.d(TAG, "Something went wrong...")
                    Log.d(TAG, response.raw().toString())
                }
            }

            override fun onFailure(call: Call<UploadProductResponse>, t: Throwable) {
                Log.d(TAG, "FAILURE")
            }

        })
        return postProductResult
    }

    fun predict(category: RequestBody, image: MultipartBody.Part): LiveData<Result<List<String>>> {
        predictResult.value = Result.Loading

        val client = api.predict(category, image)
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
                predictResult.value = Result.Error("ups ! Failure : ${t.message}")
            }

        })
        return predictResult
    }

    companion object {
        private val TAG = ProductRepository::class.java.simpleName

        @Volatile
        private var instance: ProductRepository? = null

        fun getInstance(
            api: ApiService,
            productDao: ProductDao,
            appExecutors: AppExecutors
        ): ProductRepository =
            instance ?: synchronized(this) {
                instance ?: ProductRepository(api, productDao, appExecutors)
            }.also { instance = it }
    }
}
