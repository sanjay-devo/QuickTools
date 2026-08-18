package com.indiacybercafe.quicktools

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.indiacybercafe.quicktools.databinding.ActivityToolsBinding

class ToolsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityToolsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Enable edge-to-edge
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        // 2. Setup ViewBinding
        binding = ActivityToolsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoryName = intent.getStringExtra("category_name") ?: "Tools"
        binding.tvHeaderTitle.text = categoryName.uppercase()

        // 3. Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.headerLayout.setPadding(
                20.dpToPx(),
                systemBars.top + 16.dpToPx(),
                20.dpToPx(),
                16.dpToPx()
            )
            insets
        }

        // 4. Set Font
        val typeface = android.graphics.Typeface.createFromAsset(assets, "fonts/Poppins-Bold.ttf")
        binding.tvHeaderTitle.typeface = typeface

        // 5. Back Button
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 6. Setup Tools Grid
        setupToolsGrid(categoryName)
    }

    private fun setupToolsGrid(categoryName: String) {
        val tools = ToolRepository.getToolsByCategory(categoryName)

        val adapter = CategoryAdapter(tools) { tool ->
            when (tool.name) {
                "BG Remove" -> {
                    startActivity(Intent(this, RemoveBgActivity::class.java))
                }
                "Multi-Page Studio" -> {
                    startActivity(Intent(this, MultiPageStudioActivity::class.java))
                }
                "Passport Photo" -> {
                    // Placeholder for now
                    Toast.makeText(this, "${tool.name} coming soon", Toast.LENGTH_SHORT).show()
                }
                "PVC Card Studio" -> {
                    // Placeholder for now
                    Toast.makeText(this, "${tool.name} coming soon", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "${tool.name} clicked", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.rvTools.adapter = adapter
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
