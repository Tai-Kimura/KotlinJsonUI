package com.kotlinjsonui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.kotlinjsonui.R
import com.kotlinjsonui.utils.FontCache

/**
 * Custom EditText with font support
 */
open class KjuiEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {
    
    companion object {
        const val ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android"
    }
    
    init {
        attrs?.let {
            applyCustomFont(context, it)
        }
    }
    
    /**
     * Set font programmatically
     * @param fontName Font file name in assets/fonts directory
     */
    fun setFont(fontName: String?) {
        val customFont = FontCache.getTypeface(context, fontName)
        typeface = customFont
    }
    
    /**
     * Set font from resource
     * @param resourceId String resource ID containing font name
     */
    fun setFont(resourceId: Int) {
        val fontName = context.getString(resourceId)
        setFont(fontName)
    }
    
    private fun applyCustomFont(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KjuiTextView)
        
        try {
            val fontName = typedArray.getString(R.styleable.KjuiTextView_kjui_font_name)
            
            if (fontName != null) {
                // Use custom font from attribute
                setFont(fontName)
            } else {
                // Check for textStyle attribute and apply appropriate system font
                val defTextStyle = typeface?.style ?: Typeface.NORMAL
                val textStyle = attrs.getAttributeIntValue(
                    ANDROID_SCHEMA, 
                    "textStyle", 
                    defTextStyle
                )
                applySystemFont(textStyle)
            }
        } finally {
            typedArray.recycle()
        }
    }
    
    private fun applySystemFont(textStyle: Int) {
        // Apply system font based on text style
        when (textStyle) {
            Typeface.BOLD -> setTypeface(null, Typeface.BOLD)
            Typeface.ITALIC -> setTypeface(null, Typeface.ITALIC)
            Typeface.BOLD_ITALIC -> setTypeface(null, Typeface.BOLD_ITALIC)
            else -> setTypeface(null, Typeface.NORMAL)
        }
    }
}