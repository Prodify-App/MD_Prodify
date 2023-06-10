package com.c23ps105.prodify.ui.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.c23ps105.prodify.databinding.ActivityCameraResultBinding
import com.c23ps105.prodify.helper.SessionPreferences
import com.c23ps105.prodify.helper.ViewModelFactory
import com.c23ps105.prodify.ui.viewModel.ProductViewModel
import com.c23ps105.prodify.utils.Result
import com.c23ps105.prodify.utils.reduceFileImage
import com.c23ps105.prodify.utils.rotateFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CameraResultActivity : AppCompatActivity() {
    private var getFile: File? = null
    private var result: Int? = null

    private lateinit var binding: ActivityCameraResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        result = intent.getIntExtra("result", 0)
        val pref = SessionPreferences.getInstance(dataStore)
        val factory = ViewModelFactory.getInstance(this, pref)
        val viewModel: ProductViewModel by viewModels { factory }
        if (result == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("picture", File::class.java)
            } else {
                intent.getSerializableExtra("picture")
            } as? File
            val isBackCamera = intent.getBooleanExtra("isBackCamera", true)
            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.imgCameraResult.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
        setPredictResult(viewModel)
    }

    private fun setPredictResult(viewModel: ProductViewModel) {
        if (getFile != null) {
            Log.d(TAG, "File is available")
            val title =
                binding.edtTitle.text.toString().toRequestBody("text/plain".toMediaType())
            val category =
                binding.tvCategory.text.toString().toRequestBody("text/plain".toMediaType())
            val description =
                binding.edtDescription.text.toString().toRequestBody("text/plain".toMediaType())

            val file = reduceFileImage(getFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val image = MultipartBody.Part.createFormData(
                "attachment",
                file.name,
                requestImageFile
            )

            binding.btnFinalize.setOnClickListener {
                viewModel.postProduct(image, title, category, description)
                Log.d(TAG,"Clicked")
//                viewModel.postPredict(category, image).observe(this) {
//                    when (it) {
//                        Result.Loading -> binding.progressBar.visibility = View.VISIBLE
//                        is Result.Error -> binding.progressBar.visibility = View.GONE
//                        is Result.Success -> {
//                            binding.progressBar.visibility = View.GONE
//
//                            binding.apply {
//                                edtTitle.setText(it.data.first())
//                                edtDescription.setText(it.data.last())
//                                icContainer.visibility = View.INVISIBLE
//                            }
//                        }
//                    }
//                }
            }
        } else {
            Log.d(TAG, "file not found")
        }

    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val TAG = CameraResultActivity::class.simpleName
    }
}