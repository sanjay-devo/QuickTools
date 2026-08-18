package com.indiacybercafe.quicktools

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

class PerspectiveOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val points = Array(4) { PointF() }
    
    private val paintLine = Paint().apply {
        color = Color.parseColor("#803AF8") // purple_primary
        strokeWidth = dpToPx(2f)
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    
    private val paintPoint = Paint().apply {
        color = Color.parseColor("#803AF8") // purple_primary
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintHandleOuter = Paint().apply {
        color = Color.WHITE
        strokeWidth = dpToPx(1.5f)
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    
    private val handleRadius = dpToPx(7f)
    private val touchRadius = dpToPx(36f)
    private val minDistance = dpToPx(10f)
    
    private var draggingPointIndex = -1
    private val imageRect = RectF()
    private val path = Path()

    fun resetToCropRect(newCropRect: RectF, newImageRect: RectF) {
        imageRect.set(newImageRect)
        points[0].set(newCropRect.left, newCropRect.top)
        points[1].set(newCropRect.right, newCropRect.top)
        points[2].set(newCropRect.right, newCropRect.bottom)
        points[3].set(newCropRect.left, newCropRect.bottom)
        
        validateAndClampAll()
        invalidate()
    }

    fun getPoints(): FloatArray {
        return floatArrayOf(
            points[0].x, points[0].y,
            points[1].x, points[1].y,
            points[2].x, points[2].y,
            points[3].x, points[3].y
        )
    }

    private fun dpToPx(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (imageRect.isEmpty) return

        // Draw selection area polygon
        path.reset()
        path.moveTo(points[0].x, points[0].y)
        path.lineTo(points[1].x, points[1].y)
        path.lineTo(points[2].x, points[2].y)
        path.lineTo(points[3].x, points[3].y)
        path.close()
        canvas.drawPath(path, paintLine)

        // Draw handles with white border for visibility
        for (point in points) {
            canvas.drawCircle(point.x, point.y, handleRadius, paintPoint)
            canvas.drawCircle(point.x, point.y, handleRadius, paintHandleOuter)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Clamping to image bounds - allowing exact edge capture
        val x = event.x.coerceIn(imageRect.left, imageRect.right)
        val y = event.y.coerceIn(imageRect.top, imageRect.bottom)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                draggingPointIndex = -1
                var minDistanceToHandle = Float.MAX_VALUE
                for (i in 0 until 4) {
                    val dist = distance(event.x, event.y, points[i].x, points[i].y)
                    if (dist < touchRadius && dist < minDistanceToHandle) {
                        minDistanceToHandle = dist
                        draggingPointIndex = i
                    }
                }
                if (draggingPointIndex != -1) {
                    performClick()
                    return true
                }
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                if (draggingPointIndex != -1) {
                    if (isValidMove(draggingPointIndex, x, y)) {
                        points[draggingPointIndex].set(x, y)
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                draggingPointIndex = -1
            }
        }
        return true
    }

    private fun isValidMove(index: Int, x: Float, y: Float): Boolean {
        // Loose constraint to prevent crossing but allow near-edge selection
        return when (index) {
            0 -> x < points[1].x - 5 && y < points[3].y - 5
            1 -> x > points[0].x + 5 && y < points[2].y - 5
            2 -> x > points[3].x + 5 && y > points[1].y + 5
            3 -> x < points[2].x - 5 && y > points[0].y + 5
            else -> false
        }
    }

    private fun validateAndClampAll() {
        for (i in 0 until 4) {
            points[i].x = points[i].x.coerceIn(imageRect.left, imageRect.right)
            points[i].y = points[i].y.coerceIn(imageRect.top, imageRect.bottom)
        }
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt(((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2)).toDouble()).toFloat()
    }
}
