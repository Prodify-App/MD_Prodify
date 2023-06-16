package com.c23ps105.prodify.ui.main.camera

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.c23ps105.prodify.data.remote.response.PredictDataModel
import com.c23ps105.prodify.databinding.ActivityCameraResultBinding
import com.c23ps105.prodify.helper.AuthViewModelFactory
import com.c23ps105.prodify.helper.MainViewModelFactory
import com.c23ps105.prodify.helper.PredictViewModelFactory
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.tflite.Classifier
import com.c23ps105.prodify.ui.viewModel.AuthViewModel
import com.c23ps105.prodify.ui.viewModel.MainViewModel
import com.c23ps105.prodify.ui.viewModel.PredictViewModel
import com.c23ps105.prodify.utils.Result
import com.c23ps105.prodify.utils.cat
import com.c23ps105.prodify.utils.categoryTextTransform
import com.c23ps105.prodify.utils.imageMultipart
import com.c23ps105.prodify.utils.reduceFileImage
import com.c23ps105.prodify.utils.textRequestBody
import com.google.android.material.snackbar.Snackbar
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CameraResultActivity : AppCompatActivity() {
    private var idUser: Int? = null
    private var getFile: File? = null
    private var result: Int? = null

    private lateinit var classifier: Classifier
    private lateinit var binding: ActivityCameraResultBinding

    private lateinit var mainViewModel: MainViewModel
    private lateinit var predictViewModel: PredictViewModel

    private lateinit var timer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setViewModel()

        if (result == CAMERA_X_RESULT) {
            setExtraFile()
            classifiesProduct()
        }

        binding.icCopyTitle.setOnClickListener {
            val text = binding.edtTitle.text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("title", text)
            clipboard.setPrimaryClip(clip)
            predictViewModel.setToastText("Judul berhasil dicopy ke clipboard")
        }

        binding.icCopyDescription.setOnClickListener {
            val text = binding.edtDescription.text.toString()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("description", text)
            clipboard.setPrimaryClip(clip)
            predictViewModel.setToastText("Deskripsi berhasil dicopy ke clipboard")
        }
    }

    private fun classifiesProduct() {
        getFile?.let { file ->
            classifier = Classifier(this.assets, mModelPath, mLabelPath, mInputSize)
            val predictedCategory =
                classifier.recognizeImage(BitmapFactory.decodeFile(file.path))

            val categoryTransform = categoryTextTransform(predictedCategory)
            predictViewModel.setToastText("Classified Successfully : $categoryTransform ")
            binding.tvCategoryWhite.text = categoryTransform

            var time = 60
            timer = object : CountDownTimer(60_000, 1_000) {
                override fun onTick(p0: Long) {
                    time -= 1
                    val minutes = time / 60
                    val seconds = time % 60
                    val text = String.format("%02d:%02d", minutes, seconds).chunked(1)
                        .joinToString(separator = " ")
                    binding.tvCountdown.text = text
                }

                override fun onFinish() {
                    binding.tvTitle.text = "Hasil lagi disiapin"
                    binding.tvSubtitle.text =
                        "Tunggu sebentar lagi ya, hasil judul dan deskripsi kamu bentar lagi ditulis nih."
                    binding.tvCountdown.visibility = View.GONE
                }
            }
            timer.start()

            predict(predictedCategory,file) {
                binding.edtTitle.setText(it.title)
                binding.edtDescription.setText(it.description)
            }

            binding.btnFinalize.setOnClickListener {
                val image = imageMultipart(file, "attachment")
                val title = textRequestBody(binding.edtTitle.text)
                val category = textRequestBody(binding.tvCategoryWhite.text)
                val description = textRequestBody(binding.edtDescription.text)
                val userId = textRequestBody(idUser)
                mainViewModel.postProduct(image, title, category, description, userId)
                    .observe(this) {
                        when (it) {
                            is Result.Error -> predictViewModel.setToastText(it.error)
                            Result.Loading -> predictViewModel.setToastText("Loading..")
                            is Result.Success -> {
                                predictViewModel.setToastText(it.data)
                            }
                        }
                    }
            }

        }
    }

    private fun setLoadingTime() {

    }

    private fun predict(predictedCategory: String, file: File, data: (PredictDataModel) -> Unit) {
        val category = textRequestBody(predictedCategory)
        val imagePart = imageMultipart(file, "image")

        predictViewModel.predict(category, imagePart).observe(this) {
            when (it) {
                is Result.Error -> {
                    binding.loading.visibility = View.GONE
                    predictViewModel.setToastText(it.error)
                    cat(it.error)
                }

                Result.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                }

                is Result.Success -> {
                    binding.loading.visibility = View.GONE
                    data.invoke(
                        PredictDataModel(
                            it.data[0],
                            it.data[1],
                            it.data[2]
                        )
                    )
                    cat(it.data[2])
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
        binding.progressBar.visibility = View.GONE
        result = intent.getIntExtra(CameraActivity.EXTRA_RESULT, 0)

        val factory = PredictViewModelFactory.getInstance()
        predictViewModel = viewModels<PredictViewModel> { factory }.value
        predictViewModel.getToastText().observe(this) {
            it.getContentIfNotHandled()?.let { text ->
                Log.d("snackbar", text)
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        val pref = SessionPreferences.getInstance(dataStore)
        val productFactory = MainViewModelFactory.getInstance(this, pref)
        mainViewModel = viewModels<MainViewModel> { productFactory }.value

        val aFactory = AuthViewModelFactory.getInstance(pref)
        val authViewModel = viewModels<AuthViewModel> { aFactory }.value
        authViewModel.getPreferences().observe(this) {
            idUser = it.userId
        }
    }

    companion object {
        private const val mInputSize = 224
        private const val mModelPath = "optimized_product_categorizing_model.tflite"
        private const val mLabelPath = "label.txt"
        const val CAMERA_X_RESULT = 200
    }
}