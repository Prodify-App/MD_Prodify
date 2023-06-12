package com.c23ps105.prodify.ui.viewModel

import androidx.lifecycle.ViewModel
import com.c23ps105.prodify.data.local.entity.ProductEntity
import com.c23ps105.prodify.data.repository.ProductRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {
    fun getProductFromAPI() = productRepository.getProductFromApi()
    fun getProductList() = productRepository.getProductList
    fun postProduct(
        image: MultipartBody.Part,
        title: RequestBody,
        category: RequestBody,
        description: RequestBody,
    ) = productRepository.postProduct(image, title, category, description)

    fun getProductDetail(id: Int) = productRepository.getProductDetail(id)
    fun unBookmarkProduct(product: ProductEntity) =
        productRepository.setBookmarkedProduct(product, false)

    fun bookmarkProduct(product: ProductEntity) =
        productRepository.setBookmarkedProduct(product, true)

    fun getBookmarkedProduct() = productRepository.getBookmarkedProduct()
    companion object {
        val TAG = ProductViewModel::class.java.simpleName
    }
}