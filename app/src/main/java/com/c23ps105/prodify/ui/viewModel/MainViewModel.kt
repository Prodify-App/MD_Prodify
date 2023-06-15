package com.c23ps105.prodify.ui.viewModel

import androidx.lifecycle.ViewModel
import com.c23ps105.prodify.data.repository.ProductRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class MainViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {
    fun getBlogs() = productRepository.getBlogs()
    fun getBlogsById(id: Int) = productRepository.getBlogsDetail(id)
    fun getProductList() = productRepository.getProductList()
    fun postProduct(
        image: MultipartBody.Part,
        title: RequestBody,
        category: RequestBody,
        description: RequestBody,
        idUser:RequestBody
    ) = productRepository.postProduct(image, title, category, description,idUser)

    fun getProductDetail(id: Int) = productRepository.getProductDetail(id)
    fun getBookmarkedProduct() = productRepository.getBookmarkedProduct()

    fun setToastText(text:String) = productRepository.setToastText(text)
    fun getToastText() = productRepository.toastText
}