package com.c23ps105.prodify.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c23ps105.prodify.data.repository.ProductRepository
import com.c23ps105.prodify.di.Injection
import com.c23ps105.prodify.ui.viewModel.ProductViewModel


class ProductViewModelFactory(
    private val productRepository: ProductRepository,
    private val pref: SessionPreferences
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ProductViewModelFactory? = null

        fun getInstance(context: Context, pref: SessionPreferences): ProductViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ProductViewModelFactory(Injection.provideProductRepository(context, pref), pref)
            }.also { instance = it }
    }
}