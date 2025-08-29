package com.kotlinjsonui.views

import android.content.Context
import android.util.AttributeSet
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.kotlinjsonui.R

/**
 * Circle image view for KotlinJsonUI
 * Displays images in circular shape using Coil's CircleCropTransformation
 */
class KjuiCircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : KjuiNetworkImageView(context, attrs, defStyleAttr) {
    
    var borderWidth: Float = 0f
        set(value) {
            field = value
            loadImage()
        }
    
    var borderColor: Int = android.graphics.Color.TRANSPARENT
        set(value) {
            field = value
            loadImage()
        }
    
    init {
        // Default to center crop for circle images
        scaleType = ScaleType.CENTER_CROP
        
        // Process custom attributes if available
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KjuiCircleImageView, defStyleAttr, 0)
            try {
                // Get border width
                borderWidth = typedArray.getDimension(R.styleable.KjuiCircleImageView_borderWidth, 0f)
                
                // Get border color
                borderColor = typedArray.getColor(R.styleable.KjuiCircleImageView_borderColor, android.graphics.Color.TRANSPARENT)
            } finally {
                typedArray.recycle()
            }
        }
    }
    
    /**
     * Load image with circle crop transformation
     */
    override fun loadImage() {
        val url = imageUrl
        
        if (url.isNullOrEmpty()) {
            // Show placeholder with circle crop if URL is empty
            placeholderDrawable?.let { drawable ->
                load(drawable) {
                    transformations(CircleCropTransformation())
                }
            } ?: fallbackDrawable?.let { drawable ->
                load(drawable) {
                    transformations(CircleCropTransformation())
                }
            } ?: setImageDrawable(null)
            return
        }
        
        // Load image with Coil and apply circle crop
        load(url) {
            // Circle crop transformation
            transformations(CircleCropTransformation())
            
            // Cache policy
            if (cacheEnabled) {
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
            } else {
                memoryCachePolicy(CachePolicy.DISABLED)
                diskCachePolicy(CachePolicy.DISABLED)
            }
            
            // Placeholder and error images
            placeholder(placeholderDrawable)
            error(errorDrawable ?: fallbackDrawable)
            fallback(fallbackDrawable)
            
            // Crossfade animation
            crossfade(crossfadeEnabled)
            
            // Always use FILL scale for circle images
            scale(coil.size.Scale.FILL)
        }
    }
    
    /**
     * Load local resource with circle crop
     */
    override fun setImageResource(resId: Int) {
        load(resId) {
            transformations(CircleCropTransformation())
            crossfade(crossfadeEnabled)
        }
    }
}