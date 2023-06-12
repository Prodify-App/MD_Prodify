package com.c23ps105.prodify.helper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.c23ps105.prodify.data.repository.PredictRepository
import com.c23ps105.prodify.di.Injection
import com.c23ps105.prodify.ui.viewModel.PredictViewModel

class PredictViewModelFactory private constructor(
    private val repository: PredictRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(PredictViewModel::class.java) -> {
                PredictViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var instance: PredictViewModelFactory? = null
        fun getInstance(): PredictViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: PredictViewModelFactory(Injection.providePredictRepository())
            }.also { instance = it }
    }
}