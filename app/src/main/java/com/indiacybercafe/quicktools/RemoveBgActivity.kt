package com.indiacybercafe.quicktools

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.indiacybercafe.quicktools.databinding.ActivityRemoveBgBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RemoveBgActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRemoveBgBinding
    private var selectedImageUri: Uri? = null
    private var resultBitmap: Bitmap? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            startProcessing(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        binding = ActivityRemoveBgBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val typeface = android.graphics.Typeface.createFromAsset(assets, "fonts/Poppins-Bold.ttf")
        binding.tvHeaderTitle.typeface = typeface
        binding.btnUpload.typeface = typeface
        binding.btnDownload.typeface = typeface
        binding.btnRemoveAnother.typeface = typeface

        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnSelectImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnUpload.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnDownload.setOnClickListener {
            resultBitmap?.let { saveImageToGallery(it) }
        }

        binding.btnRemoveAnother.setOnClickListener {
            showUploadState()
        }

        // Set Checkerboard Background
        binding.resultContainer.background = CheckerboardDrawable()
    }

    private fun startProcessing(uri: Uri) {
        showProcessingState()
        Glide.with(this).load(uri).into(binding.ivOriginalImage)

        lifecycleScope.launch {
            try {
                val file = getFileFromUri(uri)
                if (file == null) {
                    showError("Could not read image file")
                    return@launch
                }

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image_file", file.name, requestFile)
                val size = "auto".toRequestBody("text/plain".toMediaTypeOrNull())

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.removeBgApi.removeBackground(
                        AppConfig.REMOVE_BG_API_KEY,
                        body,
                        size
                    )
                }

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val bytes = responseBody.bytes()
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (bitmap != null) {
                            showResultState(bitmap)
                        } else {
                            showError("Failed to process image result")
                        }
                    } else {
                        showError("Empty response from server")
                    }
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Invalid API Key. Please check configuration."
                        402 -> "API limit reached. Insufficient credits."
                        400 -> "Invalid image format or parameters."
                        else -> "API Error: ${response.code()}"
                    }
                    showError(errorMsg)
                }
            } catch (e: Exception) {
                showError("Network or Processing Error: ${e.message}")
            }
        }
    }

    private fun showUploadState() {
        binding.layoutUpload.visibility = View.VISIBLE
        binding.layoutProcessing.visibility = View.GONE
        binding.layoutResult.visibility = View.GONE
        selectedImageUri = null
        resultBitmap = null
    }

    private fun showProcessingState() {
        binding.layoutUpload.visibility = View.GONE
        binding.layoutProcessing.visibility = View.VISIBLE
        binding.layoutResult.visibility = View.GONE
    }

    private fun showResultState(bitmap: Bitmap) {
        resultBitmap = bitmap
        binding.layoutUpload.visibility = View.GONE
        binding.layoutProcessing.visibility = View.GONE
        binding.layoutResult.visibility = View.VISIBLE
        binding.ivResultImage.setImageBitmap(bitmap)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        showUploadState()
    }

    private suspend fun getFileFromUri(uri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val file = File(cacheDir, "temp_image_${System.currentTimeMillis()}.png")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val timeStamp = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault()).format(Date())
        val filename = "BG-Remove_QuickTools_$timeStamp.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QuickTools")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(it, contentValues, null, null)
            }
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
