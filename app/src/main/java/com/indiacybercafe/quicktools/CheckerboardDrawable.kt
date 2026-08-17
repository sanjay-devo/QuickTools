package com.indiacybercafe.quicktools

import android.graphics.*
import android.graphics.drawable.Drawable

class CheckerboardDrawable(private val tileSize: Int = 40) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lightColor = Color.parseColor("#FFFFFF")
    private val darkColor = Color.parseColor("#E0E0E0")

    override fun draw(canvas: Canvas) {
        val width = bounds.width()
        val height = bounds.height()

        for (y in 0 until height step tileSize) {
            for (x in 0 until width step tileSize) {
                paint.color = if ((x / tileSize + y / tileSize) % 2 == 0) lightColor else darkColor
                canvas.drawRect(
                    x.toFloat(),
                    y.toFloat(),
                    (x + tileSize).toFloat(),
                    (y + tileSize).toFloat(),
                    paint
                )
            }
        }
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int = PixelFormat.OPAQUE
}