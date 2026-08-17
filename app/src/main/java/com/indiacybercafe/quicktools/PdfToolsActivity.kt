package com.indiacybercafe.quicktools

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.indiacybercafe.quicktools.databinding.ActivityPdfToolsBinding

class PdfToolsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfToolsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Enable edge-to-edge
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        // 2. Setup ViewBinding
        binding = ActivityPdfToolsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // 6. Setup PDF Tools Grid
        setupPdfToolsGrid()
    }

    private fun setupPdfToolsGrid() {
        val pdfTools = listOf(
            Category("Merge PDF", R.drawable.ic_merge),
            Category("Split PDF", R.drawable.ic_split),
            Category("Compress PDF", R.drawable.ic_compress),
            Category("PDF to JPG", R.drawable.ic_image),
            Category("JPG to PDF", R.drawable.ic_pdf),
            Category("PDF to Word", R.drawable.ic_document),
            Category("Word to PDF", R.drawable.ic_pdf),
            Category("PDF to Excel", R.drawable.ic_data),
            Category("Excel to PDF", R.drawable.ic_pdf),
            Category("Rotate PDF", R.drawable.ic_converter),
            Category("Protect PDF", R.drawable.ic_security),
            Category("Unlock PDF", R.drawable.ic_qr)
        )

        val adapter = CategoryAdapter(pdfTools) { tool ->
            Toast.makeText(this, "${tool.name} clicked", Toast.LENGTH_SHORT).show()
        }
        binding.rvPdfTools.adapter = adapter
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
