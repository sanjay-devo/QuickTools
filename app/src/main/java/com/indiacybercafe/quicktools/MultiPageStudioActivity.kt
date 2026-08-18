package com.indiacybercafe.quicktools

import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import java.text.SimpleDateFormat
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.indiacybercafe.quicktools.databinding.ActivityMultiPageStudioBinding
import com.indiacybercafe.quicktools.databinding.ItemA4PageBinding
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MultiPageStudioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMultiPageStudioBinding
    
    private data class CardState(
        var originalUri: Uri? = null,
        var croppedUri: Uri? = null,
        var cropRect: RectF? = null
    )

    private val frontCardState = CardState()
    private val backCardState = CardState()
    
    private var isSelectingFront = true

    private val pages = mutableListOf<A4PageData>()
    private val undoStack = Stack<List<A4PageData>>()
    private val redoStack = Stack<List<A4PageData>>()

    private lateinit var adapter: PageAdapter

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            if (isSelectingFront) {
                handleOriginalImage(it, true)
            } else {
                handleOriginalImage(it, false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        binding = ActivityMultiPageStudioBinding.inflate(layoutInflater)
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

        setupUI()
    }

    private fun setupUI() {
        val typeface = android.graphics.Typeface.createFromAsset(assets, "fonts/Poppins-Bold.ttf")
        binding.tvHeaderTitle.typeface = typeface

        binding.ivBack.setOnClickListener { finish() }

        binding.cardFront.setOnClickListener {
            if (frontCardState.originalUri == null) {
                isSelectingFront = true
                pickImageLauncher.launch("image/*")
            } else {
                isSelectingFront = true
                startCrop(frontCardState.originalUri!!)
            }
        }

        binding.cardBack.setOnClickListener {
            if (backCardState.originalUri == null) {
                isSelectingFront = false
                pickImageLauncher.launch("image/*")
            } else {
                isSelectingFront = false
                startCrop(backCardState.originalUri!!)
            }
        }

        binding.btnAdjustFront.setOnClickListener {
            isSelectingFront = true
            startCrop(frontCardState.originalUri!!)
        }
        binding.btnAdjustBack.setOnClickListener {
            isSelectingFront = false
            startCrop(backCardState.originalUri!!)
        }

        binding.btnAddPage.setOnClickListener { addPage() }
        binding.btnResetInputs.setOnClickListener { resetInputs() }
        binding.btnClearAll.setOnClickListener { confirmClearAll() }

        binding.cardUndo.setOnClickListener { undo() }
        binding.cardRedo.setOnClickListener { redo() }

        binding.btnCreatePdf.setOnClickListener { createPdf() }

        adapter = PageAdapter(pages) { position -> deletePage(position) }
        binding.rvPages.layoutManager = LinearLayoutManager(this)
        binding.rvPages.adapter = adapter

        updatePageCount()
    }

    private fun handleOriginalImage(uri: Uri, isFront: Boolean) {
        val state = if (isFront) frontCardState else backCardState
        state.originalUri = uri
        state.croppedUri = uri
        state.cropRect = null
        updateUploadCardUI(isFront)
    }

    private fun handleCroppedImage(uri: Uri, cropRect: RectF?, isFront: Boolean) {
        val state = if (isFront) frontCardState else backCardState
        state.croppedUri = uri
        state.cropRect = cropRect
        updateUploadCardUI(isFront)
    }

    private fun updateUploadCardUI(isFront: Boolean) {
        val state = if (isFront) frontCardState else backCardState
        val imageView = if (isFront) binding.ivFront else binding.ivBackImage
        val btnAdjust = if (isFront) binding.btnAdjustFront else binding.btnAdjustBack
        val overlay = if (isFront) binding.viewAdjustOverlayFront else binding.viewAdjustOverlayBack
        val card = if (isFront) binding.cardFront else binding.cardBack

        state.croppedUri?.let { uri ->
            imageView.imageTintList = null
            imageView.scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
            Glide.with(this)
                .load(uri)
                .into(imageView)
            btnAdjust.visibility = View.VISIBLE
            overlay.visibility = View.VISIBLE
            card.strokeColor = getColor(R.color.purple_primary)
            card.strokeWidth = (1.5 * resources.displayMetrics.density).toInt()
        }
    }

    private fun startCrop(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.JPEG)
            setCompressionQuality(90)
            setToolbarColor(getColor(R.color.purple_primary))
            setStatusBarColor(getColor(R.color.purple_primary))
            setActiveControlsWidgetColor(getColor(R.color.purple_primary))
            setToolbarWidgetColor(Color.WHITE)
            setToolbarTitle("Adjust Image")
            
            // Try to set initial crop window if available
            val state = if (isSelectingFront) frontCardState else backCardState
            state.cropRect?.let {
                // In some versions of UCrop, this might be available
                // If not, it will just fail to compile and I'll remove it.
                // But let's try the Intent approach in AdjustImageActivity instead to be safe.
            }
        }

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(85f, 53f)
            .withOptions(options)

        val intent = uCrop.getIntent(this)
        intent.setClass(this, AdjustImageActivity::class.java)

        // Pass existing crop rect to our custom activity
        val state = if (isSelectingFront) frontCardState else backCardState
        state.cropRect?.let {
            intent.putExtra("INITIAL_CROP_RECT", it)
        }

        startActivityForResult(intent, UCrop.REQUEST_CROP)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            val cropRect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data.getParcelableExtra("com.yalantis.ucrop.CropRect", RectF::class.java)
            } else {
                @Suppress("DEPRECATION")
                data.getParcelableExtra("com.yalantis.ucrop.CropRect")
            }
            resultUri?.let {
                handleCroppedImage(it, cropRect, isSelectingFront)
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Crop error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addPage() {
        val frontUri = frontCardState.croppedUri
        if (frontUri == null) {
            Toast.makeText(this, "Please upload Front Side image", Toast.LENGTH_SHORT).show()
            return
        }

        val backUri = backCardState.croppedUri
        val copies = binding.etCopies.text.toString().toIntOrNull() ?: 1
        val border = binding.etBorder.text.toString().toIntOrNull() ?: 0

        saveStateForUndo()

        repeat(copies) {
            pages.add(A4PageData(frontUri, backUri, border))
        }

        adapter.notifyDataSetChanged()
        updatePageCount()
        Toast.makeText(this, "$copies page(s) added", Toast.LENGTH_SHORT).show()
    }

    private fun deletePage(position: Int) {
        saveStateForUndo()
        pages.removeAt(position)
        adapter.notifyDataSetChanged()
        updatePageCount()
    }

    private fun resetInputs() {
        frontCardState.originalUri = null
        frontCardState.croppedUri = null
        frontCardState.cropRect = null
        backCardState.originalUri = null
        backCardState.croppedUri = null
        backCardState.cropRect = null

        binding.ivFront.setImageResource(R.drawable.ic_cloud_upload)
        binding.ivFront.imageTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#CCCCCC"))
        binding.ivFront.scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE
        
        binding.ivBackImage.setImageResource(R.drawable.ic_cloud_upload)
        binding.ivBackImage.imageTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#CCCCCC"))
        binding.ivBackImage.scaleType = android.widget.ImageView.ScaleType.CENTER_INSIDE

        binding.btnAdjustFront.visibility = View.GONE
        binding.viewAdjustOverlayFront.visibility = View.GONE
        binding.btnAdjustBack.visibility = View.GONE
        binding.viewAdjustOverlayBack.visibility = View.GONE

        binding.cardFront.strokeColor = getColor(R.color.purple_primary)
        binding.cardBack.strokeColor = Color.parseColor("#CCCCCC")
        
        val defaultStrokeWidth = (1.5 * resources.displayMetrics.density).toInt()
        binding.cardFront.strokeWidth = defaultStrokeWidth
        binding.cardBack.strokeWidth = defaultStrokeWidth

        binding.etCopies.setText("1")
        binding.etBorder.setText("0")
    }

    private fun confirmClearAll() {
        AlertDialog.Builder(this)
            .setTitle("Clear All")
            .setMessage("Are you sure you want to remove all created pages?")
            .setPositiveButton("Yes") { _, _ ->
                saveStateForUndo()
                pages.clear()
                adapter.notifyDataSetChanged()
                updatePageCount()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updatePageCount() {
        binding.tvPageCount.text = "Pages: ${pages.size}"

        val canUndo = undoStack.isNotEmpty()
        binding.cardUndo.isEnabled = canUndo
        binding.cardUndo.alpha = if (canUndo) 1.0f else 0.4f
        binding.btnUndo.imageTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#333333"))

        val canRedo = redoStack.isNotEmpty()
        binding.cardRedo.isEnabled = canRedo
        binding.cardRedo.alpha = if (canRedo) 1.0f else 0.4f
        binding.btnRedo.imageTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#333333"))
    }

    private fun saveStateForUndo() {
        undoStack.push(ArrayList(pages))
        redoStack.clear()
    }

    private fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.push(ArrayList(pages))
            pages.clear()
            pages.addAll(undoStack.pop())
            adapter.notifyDataSetChanged()
            updatePageCount()
        }
    }

    private fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.push(ArrayList(pages))
            pages.clear()
            pages.addAll(redoStack.pop())
            adapter.notifyDataSetChanged()
            updatePageCount()
        }
    }

    private fun createPdf() {
        if (pages.isEmpty()) {
            Toast.makeText(this, "No pages to create PDF", Toast.LENGTH_SHORT).show()
            return
        }

        binding.loadingOverlay.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val pdfDocument = PdfDocument()
                val pageWidth = 595 // A4 Width in points
                val pageHeight = 842 // A4 Height in points

                val cardWidthPoints = 241f // 85mm in points
                val cardHeightPoints = 150f // 53mm in points
                val xOffset = (pageWidth - cardWidthPoints) / 2f

                pages.forEachIndexed { index, pageData ->
                    val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create()
                    val page = pdfDocument.startPage(pageInfo)
                    val canvas = page.canvas

                    // Draw Front Image
                    drawPageImage(canvas, pageData.frontUri, xOffset, 100f, cardWidthPoints, cardHeightPoints, pageData.border)

                    // Draw Back Image if exists
                    pageData.backUri?.let {
                        drawPageImage(canvas, it, xOffset, 100f + cardHeightPoints + 40f, cardWidthPoints, cardHeightPoints, pageData.border)
                    }

                    pdfDocument.finishPage(page)
                }

                val timeStamp = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault()).format(Date())
                val fileName = "Multi-Page-Studio_QuickTools_$timeStamp.pdf"
                savePdf(pdfDocument, fileName)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.loadingOverlay.visibility = View.GONE
                    Toast.makeText(this@MultiPageStudioActivity, "Error creating PDF: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun drawPageImage(canvas: Canvas, uri: Uri, x: Float, y: Float, w: Float, h: Float, borderWidth: Int) {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val destRect = RectF(x, y, x + w, y + h)

        canvas.drawBitmap(bitmap, null, destRect, null)

        // Draw Border if needed
        if (borderWidth > 0) {
            val paint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = borderWidth.toFloat()
            }
            canvas.drawRect(destRect, paint)
        }

        bitmap.recycle()
    }

    private suspend fun savePdf(pdfDocument: PdfDocument, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/QuickTools")
            }
        }

        val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        uri?.let {
            contentResolver.openOutputStream(it)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()

            withContext(Dispatchers.Main) {
                binding.loadingOverlay.visibility = View.GONE
                showPdfResultDialog(it, fileName)
            }
        }
    }

    private fun showPdfResultDialog(uri: Uri, fileName: String) {
        AlertDialog.Builder(this)
            .setTitle("PDF Created Successfully")
            .setMessage("Your PDF has been saved as '$fileName' in your Documents folder.")
            .setPositiveButton("Open PDF") { _, _ -> openPdf(uri) }
            .setNegativeButton("Create Another", null)
            .show()
    }

    private fun openPdf(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        startActivity(Intent.createChooser(intent, "Open PDF"))
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    data class A4PageData(val frontUri: Uri, val backUri: Uri?, val border: Int)

    inner class PageAdapter(private val list: List<A4PageData>, private val onDelete: (Int) -> Unit) :
        RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

        inner class PageViewHolder(val itemBinding: ItemA4PageBinding) : RecyclerView.ViewHolder(itemBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
            return PageViewHolder(ItemA4PageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
            val data = list[position]
            holder.itemBinding.tvPageNumber.text = "Page ${position + 1}"
            
            Glide.with(this@MultiPageStudioActivity).load(data.frontUri).into(holder.itemBinding.ivFrontPreview)
            
            // Apply border to front card
            holder.itemBinding.cardFrontPreview.strokeWidth = data.border.dpToPx()
            holder.itemBinding.cardFrontPreview.strokeColor = Color.BLACK

            if (data.backUri != null) {
                holder.itemBinding.cardBackPreview.visibility = View.VISIBLE
                Glide.with(this@MultiPageStudioActivity).load(data.backUri).into(holder.itemBinding.ivBackPreview)
                
                // Apply border to back card
                holder.itemBinding.cardBackPreview.strokeWidth = data.border.dpToPx()
                holder.itemBinding.cardBackPreview.strokeColor = Color.BLACK
            } else {
                holder.itemBinding.cardBackPreview.visibility = View.GONE
            }

            holder.itemBinding.btnDeletePage.setOnClickListener { onDelete(position) }
        }

        override fun getItemCount(): Int = list.size
    }
}
