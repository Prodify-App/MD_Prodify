package com.c23ps105.prodify.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.c23ps105.prodify.data.local.entity.ProductEntity
import com.c23ps105.prodify.data.local.room.ProductDao
import com.c23ps105.prodify.data.remote.response.DetailProductResponse
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
    val getProductList: LiveData<Result<List<ProductEntity>>> = productResult

    private val productDetailResult = MediatorLiveData<Result<ProductEntity>>()
    private val postProductResult = MutableLiveData<String>()
    fun getProductFromApi() {
        productResult.value = Result.Loading
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
                                productDao.isProductBookmarked(detailProductResponse.id)
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
    }

    fun getProductDetail(
        id: Int
    ): LiveData<Result<ProductEntity>> {
        productDetailResult.value = Result.Loading
        val api = api.getDetailProduct(id)
        api.enqueue(object : Callback<DetailProductResponse> {
            override fun onResponse(
                call: Call<DetailProductResponse>, response: Response<DetailProductResponse>
            ) {
                if (response.isSuccessful) {
                    val product = ArrayList<ProductEntity>()
                    appExecutors.diskIO.execute {
                        response.body()?.let {
                            val isBookmarked = productDao.isProductBookmarked(it.id)
                            val detail = ProductEntity(
                                it.id,
                                it.createdAt,
                                it.updatedAt.toString(),
                                it.title,
                                it.category,
                                it.description,
                                it.imageURL,
                                isBookmarked
                            )
                            product.add(detail)
                            productDao.deleteAll()
                            productDao.insertProduct(product)

                            val localData = productDao.getProductById(it.id)
                            productDetailResult.addSource(localData) { newData: ProductEntity ->
                                productDetailResult.value = Result.Success(newData)
                            }
                        }
                    }

                } else {
                    productDetailResult.value = Result.Error("Ups! Network Error.")
                }
            }

            override fun onFailure(call: Call<DetailProductResponse>, t: Throwable) {
                productDetailResult.value = Result.Error("Response Failure : ${t.message}")
            }

        })
        return productDetailResult
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
                call: Call<UploadProductResponse>, response: Response<UploadProductResponse>
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

    fun setBookmarkedProduct(product: ProductEntity, bookmarkState: Boolean) {
        appExecutors.diskIO.execute {
            product.isBookmarked = bookmarkState
            productDao.updateProduct(product)
        }
    }

    fun getBookmarkedProduct(): LiveData<List<ProductEntity>> {
        return productDao.getBookmarkedProduct()
    }

    companion object {
        private val TAG = ProductRepository::class.java.simpleName

        @Volatile
        private var instance: ProductRepository? = null

        fun getInstance(
            api: ApiService, productDao: ProductDao, appExecutors: AppExecutors
        ): ProductRepository = instance ?: synchronized(this) {
            instance ?: ProductRepository(api, productDao, appExecutors)
        }.also { instance = it }
    }
}
