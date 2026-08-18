package com.indiacybercafe.quicktools

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.yalantis.ucrop.UCropActivity

class AdjustImageActivity : UCropActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Ensure status bar icons are white (not light)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        // Apply insets to the toolbar to prevent overlap with status bar
        val toolbar = findViewById<View>(com.yalantis.ucrop.R.id.toolbar)
        toolbar?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(v.paddingLeft, systemBars.top, v.paddingRight, v.paddingBottom)
                insets
            }
        }

        // Apply bottom insets to the root view to handle navigation bar
        val rootView = findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        rootView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, systemBars.bottom)
                insets
            }
        }
    }
}
