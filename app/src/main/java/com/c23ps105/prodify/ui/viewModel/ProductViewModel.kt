package com.c23ps105.prodify.ui.viewModel

import androidx.lifecycle.ViewModel
import com.c23ps105.prodify.data.repository.ProductRepository

class ProductViewModel(
    private val productRepository: ProductRepository,
) : ViewModel() {
    fun getProductFromAPI(token: String?) = productRepository.getProductFromApi(token)
    fun getProductList() = productRepository.getProductList()
    companion object {
        val TAG = ProductViewModel::class.java.simpleName
    }
}