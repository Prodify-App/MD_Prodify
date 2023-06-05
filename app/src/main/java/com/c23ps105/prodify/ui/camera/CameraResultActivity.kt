package com.c23ps105.prodify.ui.camera

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.c23ps105.prodify.ui.MainActivity
import com.c23ps105.prodify.databinding.ActivityCameraResultBinding
import com.c23ps105.prodify.utils.reduceFileImage
import com.c23ps105.prodify.utils.rotateFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CameraResultActivity : AppCompatActivity() {
    private var getFile: File? = null
    private var result: Int? = null

    private lateinit var binding: ActivityCameraResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setExtra()

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

    private fun setExtra() {
        result = intent.getIntExtra("result", 0)
        binding.apply {
            edtTitle.setText("Canon EAS 400D")
            edtDescription.setText("Canon EOS 4000D Kit 18-55mm BARU DAN ORIGINAL 100% GARANSI 1 TAHUN")
            icContainer.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val TAG = CameraResultActivity::class.simpleName
    }
}