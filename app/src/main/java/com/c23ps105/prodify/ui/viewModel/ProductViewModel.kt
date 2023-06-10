package com.c23ps105.prodify.ui.viewModel

import androidx.lifecycle.ViewModel
import com.c23ps105.prodify.data.repository.ProductRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProductViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {
    fun getProductFromAPI(token: String?) = productRepository.getProductFromApi(token)
    fun getProductList() = productRepository.getProductList()
    fun postProduct(
        image: MultipartBody.Part,
        title: RequestBody,
        category: RequestBody,
        description: RequestBody,
    ) = productRepository.postProduct(image, title, category, description)

    fun postPredict(
        category: RequestBody,
        image: MultipartBody.Part
    ) = productRepository.predict(category, image)

    companion object {
        val TAG = ProductViewModel::class.java.simpleName
    }
}