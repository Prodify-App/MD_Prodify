package com.c23ps105.prodify.ui.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.c23ps105.prodify.data.local.entity.ProductEntity
import com.c23ps105.prodify.data.repository.ProductRepository
import kotlinx.coroutines.launch

class BookmarkViewModel(private val repos: ProductRepository) : ViewModel() {
    private val productData = MutableLiveData<ProductEntity>()

    fun setProductData(product: ProductEntity) {
        productData.value = product
    }

    val bookmarkStatus = productData.switchMap {
        repos.isProductBookmarkAlready(it.id)
    }

    fun changeBookmark(productDetail: ProductEntity) {
        viewModelScope.launch {
            if (bookmarkStatus.value as Boolean) {
                unBookmarkProduct(productDetail)
            } else {
                bookmarkProduct(productDetail)
            }
        }
    }

    private fun unBookmarkProduct(product: ProductEntity) =
        repos.setBookmarkedProduct(product, false)

    private fun bookmarkProduct(product: ProductEntity) =
        repos.setBookmarkedProduct(product, true)


    fun getBookmarkedProduct() = repos.getBookmarkedProduct()

}