package com.c23ps105.prodify.ui.viewModel

import androidx.lifecycle.ViewModel
import com.c23ps105.prodify.data.repository.PredictRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PredictViewModel(
    private val repository: PredictRepository,
) : ViewModel() {
    fun predict(category: RequestBody, image: MultipartBody.Part) =
        repository.predict(category, image)

    fun getToastText() = repository.toastText
    fun setToastText(text: String) = repository.setToastText(text)
}