package com.c23ps105.prodify.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c23ps105.prodify.data.repository.ProductRepository
import com.c23ps105.prodify.di.Injection
import com.c23ps105.prodify.ui.viewModel.BookmarkViewModel
import com.c23ps105.prodify.ui.viewModel.MainViewModel


class MainViewModelFactory(
    private val productRepository: ProductRepository,
    private val pref: SessionPreferences
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(productRepository) as T
        } else if(modelClass.isAssignableFrom(BookmarkViewModel::class.java)){
            return BookmarkViewModel(productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: MainViewModelFactory? = null

        fun getInstance(context: Context, pref: SessionPreferences): MainViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(Injection.provideProductRepository(context, pref), pref)
            }.also { instance = it }
    }
}