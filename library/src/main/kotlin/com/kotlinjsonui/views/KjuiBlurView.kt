package com.kotlinjsonui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.kotlinjsonui.R

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
    
    private fun applyBlur(bitmap: Bitmap) {
        try {
            stackBlur(bitmap, blurRadius.toInt().coerceAtLeast(1))
        } catch (e: Exception) {
            // Blur failed, use unblurred bitmap
        }
    }

    /**
     * In-place stack blur (Mario Klingemann's algorithm) — a close visual
     * approximation of a Gaussian blur, replacing the deprecated
     * RenderScript ScriptIntrinsicBlur. Runs on the downsampled snapshot
     * bitmap, so the pixel count is small (width/height already divided by
     * [downsampleFactor]).
     */
    private fun stackBlur(bitmap: Bitmap, radius: Int) {
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        val div = radius + radius + 1
        val divSum = ((div + 1) shr 1).let { it * it }
        val dv = IntArray(256 * divSum) { it / divSum }

        val r = IntArray(w * h)
        val g = IntArray(w * h)
        val b = IntArray(w * h)
        val a = IntArray(w * h)
        val vmin = IntArray(maxOf(w, h))

        // Horizontal pass
        var yi = 0
        for (y in 0 until h) {
            var rSum = 0; var gSum = 0; var bSum = 0; var aSum = 0
            var rInSum = 0; var gInSum = 0; var bInSum = 0; var aInSum = 0
            var rOutSum = 0; var gOutSum = 0; var bOutSum = 0; var aOutSum = 0
            val stack = Array(div) { IntArray(4) }
            for (i in -radius..radius) {
                val p = pixels[yi + i.coerceIn(0, w - 1)]
                val sir = stack[i + radius]
                sir[0] = (p shr 16) and 0xff
                sir[1] = (p shr 8) and 0xff
                sir[2] = p and 0xff
                sir[3] = (p ushr 24) and 0xff
                val rbs = radius + 1 - kotlin.math.abs(i)
                rSum += sir[0] * rbs; gSum += sir[1] * rbs; bSum += sir[2] * rbs; aSum += sir[3] * rbs
                if (i > 0) {
                    rInSum += sir[0]; gInSum += sir[1]; bInSum += sir[2]; aInSum += sir[3]
                } else {
                    rOutSum += sir[0]; gOutSum += sir[1]; bOutSum += sir[2]; aOutSum += sir[3]
                }
            }
            var stackPointer = radius
            for (x in 0 until w) {
                r[yi] = dv[rSum]; g[yi] = dv[gSum]; b[yi] = dv[bSum]; a[yi] = dv[aSum]
                rSum -= rOutSum; gSum -= gOutSum; bSum -= bOutSum; aSum -= aOutSum
                val stackStart = stackPointer - radius + div
                var sir = stack[stackStart % div]
                rOutSum -= sir[0]; gOutSum -= sir[1]; bOutSum -= sir[2]; aOutSum -= sir[3]
                if (y == 0) vmin[x] = (x + radius + 1).coerceAtMost(w - 1)
                val p = pixels[y * w + vmin[x]]
                sir[0] = (p shr 16) and 0xff
                sir[1] = (p shr 8) and 0xff
                sir[2] = p and 0xff
                sir[3] = (p ushr 24) and 0xff
                rInSum += sir[0]; gInSum += sir[1]; bInSum += sir[2]; aInSum += sir[3]
                rSum += rInSum; gSum += gInSum; bSum += bInSum; aSum += aInSum
                stackPointer = (stackPointer + 1) % div
                sir = stack[stackPointer % div]
                rOutSum += sir[0]; gOutSum += sir[1]; bOutSum += sir[2]; aOutSum += sir[3]
                rInSum -= sir[0]; gInSum -= sir[1]; bInSum -= sir[2]; aInSum -= sir[3]
                yi++
            }
        }

        // Vertical pass
        for (x in 0 until w) {
            var rSum = 0; var gSum = 0; var bSum = 0; var aSum = 0
            var rInSum = 0; var gInSum = 0; var bInSum = 0; var aInSum = 0
            var rOutSum = 0; var gOutSum = 0; var bOutSum = 0; var aOutSum = 0
            val stack = Array(div) { IntArray(4) }
            var yp = -radius * w
            for (i in -radius..radius) {
                val idx = yp.coerceAtLeast(0) + x
                val sir = stack[i + radius]
                sir[0] = r[idx]; sir[1] = g[idx]; sir[2] = b[idx]; sir[3] = a[idx]
                val rbs = radius + 1 - kotlin.math.abs(i)
                rSum += r[idx] * rbs; gSum += g[idx] * rbs; bSum += b[idx] * rbs; aSum += a[idx] * rbs
                if (i > 0) {
                    rInSum += sir[0]; gInSum += sir[1]; bInSum += sir[2]; aInSum += sir[3]
                } else {
                    rOutSum += sir[0]; gOutSum += sir[1]; bOutSum += sir[2]; aOutSum += sir[3]
                }
                if (i < h - 1) yp += w
            }
            var yiV = x
            var stackPointer = radius
            for (y in 0 until h) {
                pixels[yiV] = (dv[aSum] shl 24) or (dv[rSum] shl 16) or (dv[gSum] shl 8) or dv[bSum]
                rSum -= rOutSum; gSum -= gOutSum; bSum -= bOutSum; aSum -= aOutSum
                val stackStart = stackPointer - radius + div
                var sir = stack[stackStart % div]
                rOutSum -= sir[0]; gOutSum -= sir[1]; bOutSum -= sir[2]; aOutSum -= sir[3]
                if (x == 0) vmin[y] = (y + radius + 1).coerceAtMost(h - 1) * w
                val idx = x + vmin[y]
                sir[0] = r[idx]; sir[1] = g[idx]; sir[2] = b[idx]; sir[3] = a[idx]
                rInSum += sir[0]; gInSum += sir[1]; bInSum += sir[2]; aInSum += sir[3]
                rSum += rInSum; gSum += gInSum; bSum += bInSum; aSum += aInSum
                stackPointer = (stackPointer + 1) % div
                sir = stack[stackPointer]
                rOutSum += sir[0]; gOutSum += sir[1]; bOutSum += sir[2]; aOutSum += sir[3]
                rInSum -= sir[0]; gInSum -= sir[1]; bInSum -= sir[2]; aInSum -= sir[3]
                yiV += w
            }
        }

        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
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