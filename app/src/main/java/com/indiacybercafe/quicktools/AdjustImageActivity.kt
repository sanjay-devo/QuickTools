package com.indiacybercafe.quicktools

import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import com.yalantis.ucrop.view.UCropView
import java.io.File
import java.io.FileOutputStream

class AdjustImageActivity : UCropActivity() {

    private var isPerspectiveMode = false
    private lateinit var perspectiveOverlay: PerspectiveOverlayView
    private lateinit var uCropView: UCropView
    private lateinit var btnPerspective: TextView

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

        setupPerspectiveCrop()
    }

    private fun setupPerspectiveCrop() {
        uCropView = findViewById(com.yalantis.ucrop.R.id.ucrop)
        
        // Add Perspective Overlay
        perspectiveOverlay = PerspectiveOverlayView(this).apply {
            visibility = View.GONE
        }
        val overlayParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        uCropView.addView(perspectiveOverlay, overlayParams)

        // Add Perspective Button to controls
        val wrapperStates = findViewById<LinearLayout>(com.yalantis.ucrop.R.id.wrapper_states)
        btnPerspective = TextView(this).apply {
            text = "Perspective Crop"
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(16, 0, 16, 0)
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
            setOnClickListener {
                togglePerspectiveMode()
            }
        }
        wrapperStates?.addView(btnPerspective)

        // Initialize overlay bounds when image is ready
        uCropView.post {
            updateOverlayBounds()
        }
    }

    private fun togglePerspectiveMode() {
        isPerspectiveMode = !isPerspectiveMode
        if (isPerspectiveMode) {
            perspectiveOverlay.visibility = View.VISIBLE
            btnPerspective.setTextColor(Color.parseColor("#803AF8")) // purple_primary
            uCropView.overlayView.visibility = View.GONE
            uCropView.cropImageView.isScaleEnabled = false
            uCropView.cropImageView.isRotateEnabled = false
            
            // Sync with current crop rect and image bounds
            val imageView = uCropView.cropImageView
            val drawable = imageView.drawable
            if (drawable != null) {
                val corners = floatArrayOf(
                    0f, 0f,
                    drawable.intrinsicWidth.toFloat(), 0f,
                    drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat(),
                    0f, drawable.intrinsicHeight.toFloat()
                )
                imageView.imageMatrix.mapPoints(corners)
                
                var minX = corners[0]; var maxX = corners[0]
                var minY = corners[1]; var maxY = corners[1]
                for (i in 1..3) {
                    minX = minOf(minX, corners[i*2])
                    maxX = maxOf(maxX, corners[i*2])
                    minY = minOf(minY, corners[i*2+1])
                    maxY = maxOf(maxY, corners[i*2+1])
                }
                val finalImgRect = RectF(minX, minY, maxX, maxY)
                finalImgRect.offset(imageView.left.toFloat(), imageView.top.toFloat())
                
                val cropRect = uCropView.overlayView.cropViewRect
                perspectiveOverlay.resetToCropRect(cropRect, finalImgRect)
            }
        } else {
            perspectiveOverlay.visibility = View.GONE
            btnPerspective.setTextColor(Color.WHITE)
            uCropView.overlayView.visibility = View.VISIBLE
            uCropView.cropImageView.isScaleEnabled = true
            uCropView.cropImageView.isRotateEnabled = true
        }
    }

    private fun updateOverlayBounds() {
        val imageView = uCropView.cropImageView
        val drawable = imageView.drawable ?: return
        val rect = RectF(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
        imageView.imageMatrix.mapRect(rect)
        rect.offset(imageView.left.toFloat(), imageView.top.toFloat())
        
        // We don't call setImageRect anymore, we use resetToCropRect when toggling
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == com.yalantis.ucrop.R.id.menu_crop) {
            if (isPerspectiveMode) {
                applyPerspectiveCrop()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyPerspectiveCrop() {
        val sourceUri = intent.getParcelableExtra<Uri>(UCrop.EXTRA_INPUT_URI) ?: return
        val outputUri = intent.getParcelableExtra<Uri>(UCrop.EXTRA_OUTPUT_URI) ?: return
        
        val points = perspectiveOverlay.getPoints()
        val imageView = uCropView.cropImageView
        
        val matrix = imageView.imageMatrix
        val invMatrix = Matrix()
        matrix.invert(invMatrix)
        
        val bitmapPoints = FloatArray(8)
        // Offset points relative to imageView coordinate system before inverting to bitmap space
        val tempPoints = FloatArray(8)
        for (i in 0 until 4) {
            tempPoints[i * 2] = points[i * 2] - imageView.left
            tempPoints[i * 2 + 1] = points[i * 2 + 1] - imageView.top
        }
        invMatrix.mapPoints(bitmapPoints, tempPoints)

        // Perform transformation on original bitmap
        try {
            val inputStream = contentResolver.openInputStream(sourceUri) ?: return
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Target aspect ratio 85:53
            val width = 1700 // Higher resolution for quality
            val height = 1060
            val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(resultBitmap)
            canvas.drawColor(Color.WHITE) // Background for transparent areas if any
            
            val dstPoints = floatArrayOf(
                0f, 0f,
                width.toFloat(), 0f,
                width.toFloat(), height.toFloat(),
                0f, height.toFloat()
            )
            
            val transMatrix = Matrix()
            // Map the selected 4 points from original bitmap to the destination rectangle
            transMatrix.setPolyToPoly(bitmapPoints, 0, dstPoints, 0, 4)
            
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(originalBitmap, transMatrix, paint)
            
            val outFile = File(outputUri.path ?: return)
            val outStream = FileOutputStream(outFile)
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.close()

            val resultIntent = android.content.Intent()
            resultIntent.putExtra(UCrop.EXTRA_OUTPUT_URI, outputUri)
            resultIntent.putExtra("PERSPECTIVE_POINTS", bitmapPoints)
            // Provide a RectF to satisfy the return contract expected by MultiPageStudioActivity
            resultIntent.putExtra("com.yalantis.ucrop.CropRect", RectF(0f, 0f, width.toFloat(), height.toFloat()))
            
            setResult(RESULT_OK, resultIntent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            setResult(UCrop.RESULT_ERROR, android.content.Intent().putExtra(UCrop.EXTRA_ERROR, e))
            finish()
        }
    }
}
