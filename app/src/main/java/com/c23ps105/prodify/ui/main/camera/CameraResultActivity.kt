package com.c23ps105.prodify.ui.main.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.c23ps105.prodify.databinding.ActivityCameraResultBinding
import com.c23ps105.prodify.helper.PredictViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.helper.ProductViewModelFactory
import com.c23ps105.prodify.tflite.Classifier
import com.c23ps105.prodify.ui.viewModel.PredictViewModel
import com.c23ps105.prodify.ui.viewModel.ProductViewModel
import com.c23ps105.prodify.utils.categoryTextTransform
import com.c23ps105.prodify.utils.imageMultipart
import com.c23ps105.prodify.utils.reduceFileImage
import com.c23ps105.prodify.utils.textRequestBody
import com.c23ps105.prodify.utils.Result
import com.google.android.material.snackbar.Snackbar
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CameraResultActivity : AppCompatActivity() {
    private var getFile: File? = null
    private var result: Int? = null

    private lateinit var classifier: Classifier
    private lateinit var binding: ActivityCameraResultBinding

    private lateinit var productViewModel: ProductViewModel
    private lateinit var predictViewModel: PredictViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()

        if (result == CAMERA_X_RESULT) {
            setExtraFile()
            predict()
        }

        binding.btnFinalize.setOnClickListener {
            getFile?.let {
                val image = imageMultipart(it, "attachment")
                val title = textRequestBody(binding.edtTitle.text)
                val category = textRequestBody(binding.tvCategoryWhite.text)
                val description = textRequestBody(binding.edtDescription.text)
                productViewModel.postProduct(image, title, category, description)
            }
        }
    }

    private fun predict() {
        getFile?.let { file ->
            classifier = Classifier(this.assets, mModelPath, mLabelPath, mInputSize)
            val predictedCategory =
                classifier.recognizeImage(BitmapFactory.decodeFile(file.path))

            val categoryTransform = categoryTextTransform(predictedCategory)
            predictViewModel.setToastText("Classified Successfully : $categoryTransform ")
            binding.tvCategoryWhite.text = categoryTransform

            val category = textRequestBody(predictedCategory)
            val imagePart = imageMultipart(file, "image")
            predictViewModel.predict(category, imagePart).observe(this) {
                when (it) {
                    is Result.Error -> {
                        binding.loading.visibility = View.GONE
                        predictViewModel.setToastText(it.error)
                    }

                    Result.Loading -> {
                        binding.loading.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.loading.visibility = View.GONE
                        binding.edtTitle.setText(it.data.first())
                        binding.edtDescription.setText(it.data.last())
                    }
                }
            }
        }
    }

    private fun setExtraFile() {
        val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(CameraActivity.EXTRA_FILE, File::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(CameraActivity.EXTRA_FILE)
        } as? File

        myFile?.let {
            val file = reduceFileImage(it)
            getFile = file
            binding.imgCameraResult.setImageBitmap(BitmapFactory.decodeFile(file.path))
        }
    }

    private fun setViewModel() {
        result = intent.getIntExtra(CameraActivity.EXTRA_RESULT, 0)

        val factory = PredictViewModelFactory.getInstance()
        predictViewModel = viewModels<PredictViewModel> { factory }.value
        predictViewModel.getToastText().observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        val pref = SessionPreferences.getInstance(dataStore)
        val productFactory = ProductViewModelFactory.getInstance(this, pref)
        productViewModel = viewModels<ProductViewModel> { productFactory }.value
    }

    companion object {
        private const val mInputSize = 224
        private const val mModelPath = "optimized_product_categorizing_model.tflite"
        private const val mLabelPath = "label.txt"
        const val CAMERA_X_RESULT = 200
    }
}