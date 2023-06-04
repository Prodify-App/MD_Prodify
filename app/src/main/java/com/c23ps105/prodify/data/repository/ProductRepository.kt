package com.c23ps105.prodify.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.c23ps105.prodify.data.local.entity.ProductEntity
import com.c23ps105.prodify.data.local.room.ProductDao
import com.c23ps105.prodify.data.remote.response.DetailProductResponse
import com.c23ps105.prodify.data.remote.retrofit.product.ApiProductService
import com.c23ps105.prodify.utils.AppExecutors
import com.c23ps105.prodify.utils.Result
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductRepository private constructor(
    private val api: ApiProductService,
    private val productDao: ProductDao,
    private val appExecutors: AppExecutors
) {

    private val productResult = MediatorLiveData<Result<List<ProductEntity>>>()
    private val detailResult = MediatorLiveData<Result<List<ProductEntity>>>()

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
                                detailProductResponse.updatedAt,
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

    companion object {
        private val TAG = ProductRepository::class.java.simpleName

        @Volatile
        private var instance: ProductRepository? = null

        fun getInstance(
            api: ApiProductService,
            productDao: ProductDao,
            appExecutors: AppExecutors
        ): ProductRepository =
            instance ?: synchronized(this) {
                instance ?: ProductRepository(api, productDao, appExecutors)
            }.also { instance = it }
    }
}
