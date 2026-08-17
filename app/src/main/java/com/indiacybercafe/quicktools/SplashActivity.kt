package com.indiacybercafe.quicktools

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.indiacybercafe.quicktools.databinding.ActivitySplashBinding

/**
 * SplashActivity handles the professional full-screen splash screen.
 * Transitions to MainActivity after a short delay.
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Install SplashScreen API for Android 12+ compatibility
        installSplashScreen()
        
        super.onCreate(savedInstanceState)

        // 2. Enable true edge-to-edge and hide system bars
        enableEdgeToEdge()
        hideSystemUI()

        // 3. Setup ViewBinding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 4. Load splash image from assets
        loadSplashImage()

        // 5. Navigate after 1500ms
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToMain()
        }, 1500)
    }

    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun loadSplashImage() {
        try {
            assets.open("splash.png").use { inputStream ->
                val drawable = Drawable.createFromStream(inputStream, null)
                binding.splashImage.setImageDrawable(drawable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        
        @Suppress("DEPRECATION")
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        
        finish()
    }
}
