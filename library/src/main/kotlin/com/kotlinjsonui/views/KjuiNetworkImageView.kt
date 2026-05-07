package com.kotlinjsonui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import coil.load
import coil.request.CachePolicy
import com.kotlinjsonui.R

/**
 * Network image view for KotlinJsonUI
 * Supports loading images from URLs using Coil
 */
open class KjuiNetworkImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    protected var imageUrl: String? = null
    protected var placeholderDrawable: Drawable? = null
    protected var errorDrawable: Drawable? = null
    protected var fallbackDrawable: Drawable? = null
    
    var url: String? = null
        set(value) {
            field = value
            imageUrl = value
            loadImage()
        }
    
    var placeholderImage: Int = 0
        set(value) {
            field = value
            if (value != 0) {
                placeholderDrawable = AppCompatResources.getDrawable(context, value)
                loadImage()
            }
        }
    
    var errorImage: Int = 0
        set(value) {
            field = value
            if (value != 0) {
                errorDrawable = AppCompatResources.getDrawable(context, value)
                loadImage()
            }
        }
    
    var defaultImage: Int = 0
        set(value) {
            field = value
            if (value != 0) {
                fallbackDrawable = AppCompatResources.getDrawable(context, value)
                // Also use as placeholder and error if not set
                if (placeholderDrawable == null) {
                    placeholderDrawable = fallbackDrawable
                }
                if (errorDrawable == null) {
                    errorDrawable = fallbackDrawable
                }
                loadImage()
            }
        }
    
    var cornerRadius: Float = 0f
        set(value) {
            field = value
            loadImage()
        }
    
    var crossfadeEnabled: Boolean = true
        set(value) {
            field = value
            loadImage()
        }
    
    var cacheEnabled: Boolean = true
        set(value) {
            field = value
            loadImage()
        }

    init {
        scaleType = ScaleType.CENTER_CROP
        
        // Process custom attributes if available
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KjuiNetworkImageView, defStyleAttr, 0)
            try {
                // Get URL
                typedArray.getString(R.styleable.KjuiNetworkImageView_url)?.let {
                    url = it
                }
                
                // Get placeholder image
                typedArray.getResourceId(R.styleable.KjuiNetworkImageView_placeholderImage, 0).let {
                    if (it != 0) placeholderImage = it
                }
                
                // Get error image
                typedArray.getResourceId(R.styleable.KjuiNetworkImageView_errorImage, 0).let {
                    if (it != 0) errorImage = it
                }
                
                // Get default image (fallback)
                typedArray.getResourceId(R.styleable.KjuiNetworkImageView_defaultImage, 0).let {
                    if (it != 0) defaultImage = it
                }
                
                // Get corner radius
                cornerRadius = typedArray.getDimension(R.styleable.KjuiNetworkImageView_cornerRadius, 0f)
                
                // Get crossfade enabled
                crossfadeEnabled = typedArray.getBoolean(R.styleable.KjuiNetworkImageView_crossfadeEnabled, true)
                
                // Get cache enabled
                cacheEnabled = typedArray.getBoolean(R.styleable.KjuiNetworkImageView_cacheEnabled, true)
            } finally {
                typedArray.recycle()
            }
        }
    }

    /**
     * Set image URL and load
     */
    fun setImageURI(url: String?) {
        this.url = url
    }


    /**
     * Load image using Coil
     */
    protected open fun loadImage() {
        val url = imageUrl
        
        if (url.isNullOrEmpty()) {
            // Show placeholder if URL is empty
            placeholderDrawable?.let {
                setImageDrawable(it)
            } ?: fallbackDrawable?.let {
                setImageDrawable(it)
            } ?: setImageDrawable(null)
            return
        }

        // Load image with Coil
        load(url) {
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
            
            // Corner radius transformation
            if (cornerRadius > 0) {
                transformations(coil.transform.RoundedCornersTransformation(cornerRadius))
            }
            
            // Crossfade animation
            crossfade(crossfadeEnabled)
            
            // Scale type
            when (scaleType) {
                ScaleType.CENTER_CROP -> {
                    scale(coil.size.Scale.FILL)
                }
                ScaleType.FIT_CENTER -> {
                    scale(coil.size.Scale.FIT)
                }
                ScaleType.CENTER_INSIDE -> {
                    scale(coil.size.Scale.FIT)
                }
                else -> {
                    scale(coil.size.Scale.FILL)
                }
            }
        }
    }
}