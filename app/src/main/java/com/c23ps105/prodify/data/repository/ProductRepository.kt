package com.c23ps105.prodify.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.c23ps105.prodify.data.DetailUserModel
import com.c23ps105.prodify.data.local.entity.ProductEntity
import com.c23ps105.prodify.data.local.room.ProductDao
import com.c23ps105.prodify.data.remote.response.BlogsItem
import com.c23ps105.prodify.data.remote.response.BlogsResponse
import com.c23ps105.prodify.data.remote.response.ProductResponse
import com.c23ps105.prodify.data.remote.response.ProductsItem
import com.c23ps105.prodify.data.remote.response.UploadProductResponse
import com.c23ps105.prodify.data.remote.retrofit.ApiService
import com.c23ps105.prodify.utils.AppExecutors
import com.c23ps105.prodify.utils.Event
import com.c23ps105.prodify.utils.Result
import com.c23ps105.prodify.utils.cat
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductRepository private constructor(
    private val api: ApiService,
    private val productDao: ProductDao,
    private val appExecutors: AppExecutors,
) {
    private val productResult = MediatorLiveData<Result<List<ProductEntity>>>()

    private val productDetailResult = MediatorLiveData<Result<DetailUserModel>>()
    private val postProductResult = MutableLiveData<String>()

    private val blogResult = MediatorLiveData<Result<List<BlogsItem>>>()
    private val blogDetailResult = MediatorLiveData<Result<BlogsItem>>()

    private val _toastText = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = _toastText
    fun getBlogs(): LiveData<Result<List<BlogsItem>>> {
        blogResult.value = Result.Loading
        api.getBlogs().enqueue(object : Callback<BlogsResponse> {
            override fun onResponse(call: Call<BlogsResponse>, response: Response<BlogsResponse>) {
                if (response.isSuccessful) {
                    val item = response.body()?.articles?.map {
                        val test = BlogsItem(
                            it.blogId,
                            it.imageURL,
                            it.description,
                            it.title
                        )
                        test
                    }
                    blogResult.value = Result.Success(item as List)
                } else {
                    blogResult.value = Result.Loading

                }
            }

            override fun onFailure(call: Call<BlogsResponse>, t: Throwable) {
                blogResult.value = Result.Loading
            }

        })
        return blogResult
    }

    fun getBlogsDetail(id: Int): LiveData<Result<BlogsItem>> {
        blogDetailResult.value = Result.Loading
        api.getBlogsById(id).enqueue(object : Callback<BlogsItem> {
            override fun onResponse(call: Call<BlogsItem>, response: Response<BlogsItem>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val body = BlogsItem(it.blogId, it.imageURL, it.description, it.title)
                        blogDetailResult.value = Result.Success(body)
                    }
                } else {
                    blogDetailResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<BlogsItem>, t: Throwable) {
                blogDetailResult.value = Result.Error(t.message.toString())
            }

        })
        return blogDetailResult
    }

    fun getProductList(): LiveData<Result<List<ProductEntity>>> {
        productResult.value = Result.Loading
        val client = api.getAllProduct()
        client.enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>,
            ) {
                if (response.isSuccessful) {
                    val listBody = response.body()?.listProducts
                    val productList = ArrayList<ProductEntity>()
                    appExecutors.diskIO.execute {
                        listBody?.forEach { product ->
                            val isBookmarked =
                                productDao.isProductBookmarked(product.idProduct)
                            val entity = ProductEntity(
                                product.idProduct,
                                product.createdAt,
                                product.updatedAt.toString(),
                                product.title,
                                product.category,
                                product.description,
                                product.imageURL,
                                isBookmarked
                            )
                            productList.add(entity)
                        }
                        productDao.deleteAll()
                        productDao.insertProduct(productList)
                    }
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
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

    fun getProductDetail(
        id: Int,
    ): LiveData<Result<DetailUserModel>> {
        productDetailResult.value = Result.Loading
        api.getDetailProduct(id).enqueue(object : Callback<ProductsItem> {
            override fun onResponse(
                call: Call<ProductsItem>,
                response: Response<ProductsItem>,
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        val data = DetailUserModel(
                            it.idProduct,
                            it.idUser,
                            it.title,
                            it.category,
                            it.description,
                            it.imageURL.toString()
                        )
                        productDetailResult.value = Result.Success(data)
                    }
                } else {
                    cat("error")
                    Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<ProductsItem>, t: Throwable) {
                productDetailResult.value = Result.Error(t.message.toString())
            }

        })
        return productDetailResult
    }

    fun postProduct(
        image: MultipartBody.Part,
        title: RequestBody,
        category: RequestBody,
        description: RequestBody,
        idUser: RequestBody,
    ): LiveData<String> {
        val client = api.uploadProduct(image, title, category, description, idUser)
        Log.d(TAG, client.toString())
        client.enqueue(object : Callback<UploadProductResponse> {
            override fun onResponse(
                call: Call<UploadProductResponse>, response: Response<UploadProductResponse>,
            ) {
                if (response.isSuccessful) {
                    postProductResult.value = "success !"
                } else {
                    postProductResult.value = "lengkapi data terlebih dahulu sebelum mengupload"
                }
            }

            override fun onFailure(call: Call<UploadProductResponse>, t: Throwable) {
                postProductResult.value = t.message.toString()
            }

        })
        return postProductResult
    }

    fun getBookmarkedProduct(): LiveData<List<ProductEntity>> {
        return productDao.getBookmarkedProduct()
    }

    fun setBookmarkedProduct(product: ProductEntity, bookmarkState: Boolean) {
        appExecutors.diskIO.execute {
            product.isBookmarked = bookmarkState
            productDao.updateProduct(product)
        }
    }
    fun isProductBookmarkAlready(productId: Int): LiveData<Boolean> {
        return productDao.isProductBookmarkAlready(productId)
    }

    fun setToastText(text: String) {
        _toastText.value = Event(text)
    }

    companion object {
        private val TAG = ProductRepository::class.java.simpleName

        @Volatile
        private var instance: ProductRepository? = null

        fun getInstance(
            api: ApiService, productDao: ProductDao, appExecutors: AppExecutors,
        ): ProductRepository = instance ?: synchronized(this) {
            instance ?: ProductRepository(api, productDao, appExecutors)
        }.also { instance = it }
    }
}
