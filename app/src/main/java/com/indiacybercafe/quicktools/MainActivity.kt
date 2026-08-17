package com.indiacybercafe.quicktools

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
    private var doubleBackToExitPressedOnce = false

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

        // 5. Setup Categories Grid
        setupCategoriesGrid()

        // 6. Double Press to Exit
        setupDoubleBackToExit()
    }

    private fun setupDoubleBackToExit() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    finish()
                    return
                }

                doubleBackToExitPressedOnce = true
                Toast.makeText(this@MainActivity, "Press back again to exit", Toast.LENGTH_SHORT).show()

                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            }
        })
    }

    private fun setupCategoriesGrid() {
        val categories = listOf(
            Category("PDF Tools", R.drawable.ic_pdf),
            Category("Image Tools", R.drawable.ic_image),
            Category("Document Tools", R.drawable.ic_document),
            Category("Text Tools", R.drawable.ic_text),
            Category("Calculator", R.drawable.ic_calculator),
            Category("Converters", R.drawable.ic_converter),
            Category("Security & Privacy", R.drawable.ic_security),
            Category("QR & Barcode", R.drawable.ic_qr),
            Category("Web & URL Tools", R.drawable.ic_web),
            Category("Color & Design", R.drawable.ic_color),
            Category("Developer Tools", R.drawable.ic_developer),
            Category("Date & Time", R.drawable.ic_date),
            Category("Finance Tools", R.drawable.ic_finance),
            Category("Math Tools", R.drawable.ic_math),
            Category("Social Media Tools", R.drawable.ic_social),
            Category("Audio Tools", R.drawable.ic_audio),
            Category("Video Tools", R.drawable.ic_video),
            Category("File Tools", R.drawable.ic_file),
            Category("Data Tools", R.drawable.ic_data),
            Category("Miscellaneous Tools", R.drawable.ic_misc)
        )

        val adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, ToolsActivity::class.java)
            intent.putExtra("category_name", category.name)
            startActivity(intent)
        }
        binding.rvCategories.adapter = adapter
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
