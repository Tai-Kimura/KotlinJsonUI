package com.kotlinjsonui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.kotlinjsonui.R
import com.kotlinjsonui.graphics.StackBlur

/**
 * A FrameLayout that applies blur effect to its background
 * Can contain child views that appear on top of the blurred background
 * Requires minSdk 24
 */
class KjuiBlurView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    
    private var blurRadius: Float = 10f
        set(value) {
            // Keep the historical 0-25 range (former RenderScript max radius)
            // so existing layouts render identically.
            field = value.coerceIn(0f, 25f)
            updateBlurEffect()
        }
    
    private var blurOverlayColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            updateBlurEffect()
        }
    
    private var downsampleFactor: Float = 4f
        set(value) {
            field = value.coerceIn(1f, 8f)
            updateBlurEffect()
        }
    
    private var isBlurEnabled: Boolean = true
        set(value) {
            field = value
            updateBlurEffect()
        }
    
    private var blurredBitmap: Bitmap? = null
    private var blurCanvas: Canvas? = null
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    
    init {
        // Make sure we draw on top of the blurred background
        setWillNotDraw(false)
        
        // Process custom attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KjuiBlurView, defStyleAttr, 0)
            try {
                blurRadius = typedArray.getFloat(R.styleable.KjuiBlurView_blurRadius, 10f)
                blurOverlayColor = typedArray.getColor(R.styleable.KjuiBlurView_blurOverlayColor, Color.TRANSPARENT)
                downsampleFactor = typedArray.getFloat(R.styleable.KjuiBlurView_downsampleFactor, 4f)
                isBlurEnabled = typedArray.getBoolean(R.styleable.KjuiBlurView_blurEnabled, true)
            } finally {
                typedArray.recycle()
            }
        }
        
    }
    
    override fun draw(canvas: Canvas) {
        // Only apply blur if enabled
        if (isBlurEnabled) {
            // Draw blurred background if available
            blurredBitmap?.let { bitmap ->
                canvas.save()
                canvas.scale(downsampleFactor, downsampleFactor)
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
                canvas.restore()
                
                // Draw overlay color if specified
                if (blurOverlayColor != Color.TRANSPARENT) {
                    canvas.drawColor(blurOverlayColor)
                }
            }
        }
        
        // Draw children on top
        super.draw(canvas)
    }
    
    private fun updateBlurEffect() {
        if (!isBlurEnabled) {
            blurredBitmap = null
            invalidate()
            return
        }
        
        // Get the view behind this one to blur
        val decorView = rootView
        if (decorView == null || width <= 0 || height <= 0) {
            return
        }
        
        // Create bitmap for blur at lower resolution for performance
        val scaledWidth = (width / downsampleFactor).toInt().coerceAtLeast(1)
        val scaledHeight = (height / downsampleFactor).toInt().coerceAtLeast(1)
        
        // Reuse bitmap if possible
        if (blurredBitmap?.width != scaledWidth || blurredBitmap?.height != scaledHeight) {
            blurredBitmap?.recycle()
            blurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
            blurCanvas = Canvas(blurredBitmap!!)
        }
        
        blurredBitmap?.let { bitmap ->
            blurCanvas?.let { canvas ->
                // Clear the bitmap
                bitmap.eraseColor(Color.TRANSPARENT)
                
                // Draw the background color or use parent's background
                val parentView = parent as? View
                parentView?.background?.let { drawable ->
                    canvas.save()
                    canvas.scale(1f / downsampleFactor, 1f / downsampleFactor)
                    drawable.draw(canvas)
                    canvas.restore()
                }
                
                // Apply blur in-place (pure-Kotlin stack blur — RenderScript
                // is deprecated since API 31)
                applyBlur(bitmap)
            }
        }
        
        invalidate()
    }
    
    // Blur algorithm lives in [StackBlur] (pure pixel-array stack blur,
    // JVM unit-tested); this wrapper only does the Bitmap <-> IntArray IO.
    private fun applyBlur(bitmap: Bitmap) {
        try {
            val w = bitmap.width
            val h = bitmap.height
            val pixels = IntArray(w * h)
            bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
            StackBlur.blur(pixels, w, h, blurRadius.toInt().coerceAtLeast(1))
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        } catch (e: Exception) {
            // Blur failed, use unblurred bitmap
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateBlurEffect()
    }
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateBlurEffect()
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Clean up resources
        blurredBitmap?.recycle()
        blurredBitmap = null
        blurCanvas = null
    }
}