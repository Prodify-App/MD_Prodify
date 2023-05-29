package com.c23ps105.prodify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.c23ps105.prodify.databinding.ActivityMainBinding
import com.c23ps105.prodify.ui.auth.AuthActivity
import com.c23ps105.prodify.ui.auth.AuthenticationFragment
import com.c23ps105.prodify.ui.camera.CameraActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isLogin : Boolean? = false
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        isLogin = intent.getBooleanExtra(AuthenticationFragment.EXTRA_LOGIN, false)
        Log.d("testing",isLogin.toString())
        checkLogin(isLogin!!)
        binding.fabPhotos.imageTintList = ColorStateList.valueOf(Color.WHITE)
        val navView: BottomNavigationView = binding.navView
        navView.background = null
        navView.menu.getItem(2).isEnabled = false
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_article,
                0,
                R.id.navigation_result,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.fabPhotos.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                startCameraX()
            }
        }
    }

    private fun setToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                setToast(getString(R.string.permission_denied))
            }
        }
    }

    private fun checkLogin(isLogin: Boolean) {
        if (isLogin) {
            setContentView(binding.root)
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}