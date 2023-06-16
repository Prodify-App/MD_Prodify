package com.c23ps105.prodify.data

import com.c23ps105.prodify.data.local.entity.ProductEntity
import com.c23ps105.prodify.data.remote.response.BlogsItem
import com.c23ps105.prodify.data.remote.response.PredictDataModel
import com.c23ps105.prodify.data.remote.response.PredictResponse
import com.c23ps105.prodify.data.remote.response.ProductResponse
import com.c23ps105.prodify.data.remote.response.ProductsUserItem
import com.c23ps105.prodify.ui.adapter.BlogsAdapter
import com.c23ps105.prodify.ui.adapter.ProductAdapter
import com.c23ps105.prodify.ui.adapter.ResultAdapter
import kotlinx.parcelize.Parcelize

data class DetailUserModel(
    val productId: Int,
    val userId: Int,
    val title: String,
    val category: String,
    val description: String,
    val imageURL: String,
)

data class Product(
    val adapter: ProductAdapter,
    val list: List<ProductEntity>,
)

data class UserProduct(
    val adapter: ResultAdapter,
    val list: List<ProductsUserItem>,
)

data class Blog(
    val adapter: BlogsAdapter,
    val list: List<BlogsItem>,
)

data class ProductDetail(
    val data: DetailUserModel,
)


data class PredictData(
    val data: PredictDataModel
)

data class PreferenceKey(
    val userId: Int,
    val token: String,
    val username: String,
    val email : String
)