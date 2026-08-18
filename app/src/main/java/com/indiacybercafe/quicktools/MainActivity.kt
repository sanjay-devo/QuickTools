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
        val typefaceBold = android.graphics.Typeface.createFromAsset(assets, "fonts/Poppins-Bold.ttf")
        binding.tvHeaderTitle.typeface = typefaceBold
        binding.tvTopToolsHeading.typeface = typefaceBold
        binding.tvAllCategoriesHeading.typeface = typefaceBold

        // 5. Setup Sections
        setupTopTools()
        setupCategoriesGrid()

        // 6. Double Press to Exit
        setupDoubleBackToExit()
    }

    private fun setupTopTools() {
        val topTools = ToolRepository.getTopTools()
        val adapter = TopToolAdapter(topTools) { tool ->
            when (tool.name) {
                "BG Remove" -> {
                    startActivity(Intent(this, RemoveBgActivity::class.java))
                }
                "Passport Photo" -> {
                    // Placeholder for now
                    Toast.makeText(this, "${tool.name} coming soon", Toast.LENGTH_SHORT).show()
                }
                "Multi-Page Studio" -> {
                    startActivity(Intent(this, MultiPageStudioActivity::class.java))
                }
                else -> {
                    Toast.makeText(this, "${tool.name} clicked", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.rvTopTools.adapter = adapter
    }

    private fun setupCategoriesGrid() {
        val categories = ToolRepository.getCategories()
        val adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, ToolsActivity::class.java)
            intent.putExtra("category_name", category.name)
            startActivity(intent)
        }
        binding.rvCategories.adapter = adapter
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

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
