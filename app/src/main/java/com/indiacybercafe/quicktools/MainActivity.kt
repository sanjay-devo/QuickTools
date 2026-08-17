package com.indiacybercafe.quicktools

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.indiacybercafe.quicktools.databinding.ActivityMainBinding

/**
 * Clean MainActivity showing only Hello World.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. Enable edge-to-edge
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        // 2. Setup ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Apply bottom/left/right padding to the root view
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            
            // Apply top padding (status bar) to the header
            binding.headerLayout.setPadding(
                20.dpToPx(),
                systemBars.top + 16.dpToPx(),
                20.dpToPx(),
                16.dpToPx()
            )
            insets
        }

        // 4. Set Poppins Font from assets
        val typeface = android.graphics.Typeface.createFromAsset(assets, "fonts/Poppins-Bold.ttf")
        binding.tvHeaderTitle.typeface = typeface
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
