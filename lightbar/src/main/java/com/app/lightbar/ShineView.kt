package com.app.lightbar

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

private const val ZERO = 0.0f
private const val DEFAULT_INTENSITY_PERCENT = 1f
private const val DEFAULT_SHINE_PADDING = 0f

internal class ShineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleRes) {

    private var shinePadding: Float = DEFAULT_SHINE_PADDING
        set(value) {
            field = if (value < 0f) 0f else value
        }

    private var intensityPercent: Float = DEFAULT_INTENSITY_PERCENT
        set(value) {
            field = when {
                value > 1f -> 1f
                value < 0f -> 0f
                else -> value
            }
        }

    private var path = Path()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ShineView, 0, defStyleRes).apply {
            intensityPercent =
                getFloat(R.styleable.ShineView_intensityPercent, DEFAULT_INTENSITY_PERCENT)
            shinePadding =
                getDimension(R.styleable.ShineView_shinePadding, DEFAULT_SHINE_PADDING)
            recycle()
        }

        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        path = Path().apply {
            moveTo(shinePadding, ZERO)
            lineTo(w.toFloat() - shinePadding, ZERO)
            lineTo(w.toFloat(), h.toFloat())
            lineTo(ZERO, h.toFloat())
            lineTo(shinePadding, ZERO)
            close()
        }

        val wQuite = (w / 2).toFloat()

        paint.shader = LinearGradient(
            wQuite,
            calcIntensity(h),
            wQuite,
            h.toFloat(),
            Color.WHITE,
            Color.TRANSPARENT,
            TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
    }

    private fun calcIntensity(height: Int): Float {
        // 100 * 1 - 100 = 0
        // 100 * 0.5 - 100 = -50 * (2 - 0.5 = 1.5) = -0.75
        return ((height * intensityPercent) - height) * (2 - intensityPercent)
    }
}