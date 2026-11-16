package com.kotlinjsonui.views

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.kotlinjsonui.R
import com.kotlinjsonui.graphics.StackBlur

/**
 * A FrameLayout that applies a blur effect to the content behind it.
 * Child views appear sharp on top of the blurred backdrop.
 *
 * Backdrop snapshot = the parent's background drawable plus every sibling
 * drawn BELOW this view (z-order), captured downsampled into a bitmap.
 * The snapshot refreshes on attach / size change / property change.
 *
 * Blur execution:
 * - API 31+, hardware canvas: the unblurred snapshot is drawn through a
 *   RenderNode with RenderEffect.createBlurEffect — GPU blur, no pixel
 *   readback.
 * - Otherwise: pure-Kotlin [StackBlur] on the downsampled snapshot
 *   (the pre-31 path; also covers software canvases such as View-to-bitmap
 *   capture).
 *
 * Requires minSdk 24.
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

    /** Unblurred, downsampled backdrop snapshot (source for both blur paths). */
    private var snapshotBitmap: Bitmap? = null
    private var snapshotCanvas: Canvas? = null

    /** Lazily blurred copy for the CPU path; invalidated on every re-snapshot. */
    private var cpuBlurredBitmap: Bitmap? = null

    /** GPU blur node (API 31+). */
    private var blurNode: RenderNode? = null

    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)

    init {
        // Make sure we draw on top of the blurred backdrop
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
        if (isBlurEnabled) {
            snapshotBitmap?.let { snapshot ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && canvas.isHardwareAccelerated) {
                    drawBlurredGpu(canvas, snapshot)
                } else {
                    drawBlurredCpu(canvas, snapshot)
                }

                // Draw overlay color if specified
                if (blurOverlayColor != Color.TRANSPARENT) {
                    canvas.drawColor(blurOverlayColor)
                }
            }
        }

        // Draw children on top
        super.draw(canvas)
    }

    /** GPU path: draw the unblurred snapshot through a blur RenderEffect. */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun drawBlurredGpu(canvas: Canvas, snapshot: Bitmap) {
        val node = blurNode ?: RenderNode("KjuiBlurView").also { blurNode = it }
        node.setPosition(0, 0, width, height)
        // The legacy pipeline blurred the DOWNSAMPLED bitmap and upscaled it,
        // so the effective on-screen radius was radius * downsampleFactor.
        val effectiveRadius = (blurRadius * downsampleFactor).coerceAtLeast(1f)
        node.setRenderEffect(
            RenderEffect.createBlurEffect(effectiveRadius, effectiveRadius, Shader.TileMode.CLAMP)
        )
        val recordingCanvas = node.beginRecording(width, height)
        try {
            recordingCanvas.scale(downsampleFactor, downsampleFactor)
            recordingCanvas.drawBitmap(snapshot, 0f, 0f, paint)
        } finally {
            node.endRecording()
        }
        canvas.drawRenderNode(node)
    }

    /** CPU path: stack-blur the snapshot once, cache, and draw upscaled. */
    private fun drawBlurredCpu(canvas: Canvas, snapshot: Bitmap) {
        val blurred = cpuBlurredBitmap ?: run {
            val copy = snapshot.copy(Bitmap.Config.ARGB_8888, true) ?: return
            try {
                val w = copy.width
                val h = copy.height
                val pixels = IntArray(w * h)
                copy.getPixels(pixels, 0, w, 0, 0, w, h)
                StackBlur.blur(pixels, w, h, blurRadius.toInt().coerceAtLeast(1))
                copy.setPixels(pixels, 0, w, 0, 0, w, h)
            } catch (e: Exception) {
                // Blur failed, fall back to the unblurred snapshot copy
            }
            cpuBlurredBitmap = copy
            copy
        }
        canvas.save()
        canvas.scale(downsampleFactor, downsampleFactor)
        canvas.drawBitmap(blurred, 0f, 0f, paint)
        canvas.restore()
    }

    /**
     * Rebuild the backdrop snapshot: parent background + all siblings whose
     * z-order is below this view, rendered downsampled in this view's
     * coordinate space.
     */
    private fun updateBlurEffect() {
        if (!isBlurEnabled) {
            snapshotBitmap = null
            cpuBlurredBitmap = null
            invalidate()
            return
        }

        val parentGroup = parent as? ViewGroup
        if (parentGroup == null || width <= 0 || height <= 0) {
            return
        }

        // Create bitmap for blur at lower resolution for performance
        val scaledWidth = (width / downsampleFactor).toInt().coerceAtLeast(1)
        val scaledHeight = (height / downsampleFactor).toInt().coerceAtLeast(1)

        // Reuse bitmap if possible
        if (snapshotBitmap?.width != scaledWidth || snapshotBitmap?.height != scaledHeight) {
            snapshotBitmap?.recycle()
            snapshotBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
            snapshotCanvas = Canvas(snapshotBitmap!!)
        }

        val bitmap = snapshotBitmap ?: return
        val canvas = snapshotCanvas ?: return
        bitmap.eraseColor(Color.TRANSPARENT)

        canvas.save()
        canvas.scale(1f / downsampleFactor, 1f / downsampleFactor)

        // Parent background, positioned in this view's coordinate space
        parentGroup.background?.let { drawable ->
            canvas.save()
            canvas.translate(-left.toFloat(), -top.toFloat())
            drawable.draw(canvas)
            canvas.restore()
        }

        // Siblings drawn below this view (z-order), i.e. the content the
        // blur backdrop actually sits on. Children above are excluded.
        val selfIndex = parentGroup.indexOfChild(this)
        for (i in 0 until selfIndex) {
            val sibling = parentGroup.getChildAt(i)
            if (sibling.visibility != VISIBLE) continue
            canvas.save()
            canvas.translate((sibling.left - left).toFloat(), (sibling.top - top).toFloat())
            sibling.draw(canvas)
            canvas.restore()
        }

        canvas.restore()

        // New snapshot invalidates the CPU-blurred cache
        cpuBlurredBitmap?.recycle()
        cpuBlurredBitmap = null

        invalidate()
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
        snapshotBitmap?.recycle()
        snapshotBitmap = null
        snapshotCanvas = null
        cpuBlurredBitmap?.recycle()
        cpuBlurredBitmap = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            blurNode?.discardDisplayList()
        }
        blurNode = null
    }
}
