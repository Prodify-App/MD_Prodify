package com.c23ps105.prodify.ui.camera

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.c23ps105.prodify.data.remote.retrofit.GeneratedTextResponse
import com.c23ps105.prodify.data.remote.retrofit.GenerationModelAPI
import com.c23ps105.prodify.ui.MainActivity
import com.c23ps105.prodify.databinding.ActivityCameraResultBinding
import com.c23ps105.prodify.tflite.Classifier
import com.c23ps105.prodify.utils.reduceFileImage
import com.c23ps105.prodify.utils.rotateFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CameraResultActivity : AppCompatActivity() {
    private var getFile: File? = null
    private var result: Int? = null

    private val mInputSize = 224
    private val mModelPath = "optimized_product_categorizing_model.tflite"
    private val mLabelPath = "label.txt"

    private lateinit var classifier: Classifier

    private lateinit var binding: ActivityCameraResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loading.visibility = View.VISIBLE

        setExtra()

        if (result == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("picture", File::class.java)
            } else {
                intent.getSerializableExtra("picture")
            } as? File
            val isBackCamera = intent.getBooleanExtra("isBackCamera", true)
            myFile?.let { file ->
//                rotateFile(file, isBackCamera)
                getFile = file
                binding.imgCameraResult.setImageBitmap(BitmapFactory.decodeFile(file.path))

                classifier = Classifier(this.assets, mModelPath, mLabelPath, mInputSize)
                val predictedCategory = classifier.recognizeImage(BitmapFactory.decodeFile(file.path))
                binding.tvCategory.text = predictedCategory

                val service = GenerationModelAPI()

                val categoryRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), predictedCategory)
                val imagePart = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody("image/*".toMediaTypeOrNull())
                )

                service.generateText(imagePart, categoryRequestBody).enqueue(object :
                    Callback<GeneratedTextResponse> {
                    override fun onResponse(
                        call: Call<GeneratedTextResponse>,
                        response: Response<GeneratedTextResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseData = response.body()
                            if (responseData != null) {
                                binding.edtTitle.setText(responseData.title)
                                binding.edtDescription.setText(responseData.description)
                            }

//                            if (responseData != null) {
//                                Log.i("UploadImage", "Success: ${responseData.string()}")
//                            }

                            binding.loading.visibility = View.GONE
                        } else {
                            Log.e("UploadImage", "Network request failed")
                            Toast.makeText(this@CameraResultActivity, "Fail Response", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<GeneratedTextResponse>, t: Throwable) {
                        Log.e("UploadImage", "Network request failed", t)
                        Toast.makeText(this@CameraResultActivity, "Fail Network", Toast.LENGTH_SHORT).show()
                    }

                })
            }
        }

        binding.btnFinalize.setOnClickListener {
            if (getFile != null) {
                val file = reduceFileImage(getFile as File)

//                val token = intent.getStringExtra(HomeActivity.EXTRA_TOKEN)
//                Log.d("testing", token.toString())

//                val description =
//                    binding.descStory.text.toString().toRequestBody("text/plain".toMediaType())

                val lat = "0".toRequestBody("text/plain".toMediaType())
                val lon = "0".toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()


    }

    private fun setExtra() {
        result = intent.getIntExtra("result", 0)
        binding.apply {
            edtTitle.setText("Canon EAS 400D")
            edtDescription.setText("Canon EOS 4000D Kit 18-55mm BARU DAN ORIGINAL 100% GARANSI 1 TAHUN")
            tvCategory.text = "..."
            icContainer.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val TAG = CameraResultActivity::class.simpleName
    }
}