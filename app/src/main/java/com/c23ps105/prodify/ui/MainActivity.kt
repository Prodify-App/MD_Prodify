package com.c23ps105.prodify.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.c23ps105.prodify.R
import com.c23ps105.prodify.data.SessionPreferences
import com.c23ps105.prodify.databinding.ActivityMainBinding
import com.c23ps105.prodify.ui.auth.AuthActivity
import com.c23ps105.prodify.ui.camera.CameraActivity
import com.c23ps105.prodify.ui.viewModel.AuthViewModel
import com.c23ps105.prodify.ui.viewModel.AuthViewModelFactory
import com.c23ps105.prodify.ui.viewModel.ProductViewModel
import com.c23ps105.prodify.ui.viewModel.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar?.hide()

        val pref = SessionPreferences.getInstance(dataStore)
        val factory = AuthViewModelFactory.getInstance(pref)
        val viewModel: AuthViewModel by viewModels { factory }
        viewModel.getSessionSettings().observe(this) {
            checkLogin(it)
        }

        val pFactory = ViewModelFactory.getInstance(this, pref)
        val productViewModel: ProductViewModel by viewModels { pFactory }
        productViewModel.getProductFromAPI("test")
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
        if (!isLogin) {
            Intent(this, AuthActivity::class.java).also {
                startActivity(it)
                finish()
            }
        } else {
            setContentView(binding.root)
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
                startCameraX()
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


    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun setToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}