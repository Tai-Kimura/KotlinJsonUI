package com.kotlinjsonui.utils

import android.content.Context
import android.graphics.Typeface
import java.util.concurrent.ConcurrentHashMap

/**
 * Font cache utility for managing custom fonts
 */
object FontCache {
    private val fontCache = ConcurrentHashMap<String, Typeface>()
    
    /**
     * Get typeface from cache or load it if not cached
     * @param context Application context
     * @param fontName Font file name in assets/fonts directory or null for system font
     * @return Typeface instance
     */
    @JvmStatic
    fun getTypeface(context: Context, fontName: String?): Typeface {
        if (fontName.isNullOrEmpty()) {
            return Typeface.DEFAULT
        }
        
        return fontCache.getOrPut(fontName) {
            try {
                Typeface.createFromAsset(context.assets, "fonts/$fontName")
            } catch (e: Exception) {
                // Fall back to system font if custom font not found
                Typeface.DEFAULT
            }
        }
    }
    
    /**
     * Clear font cache
     */
    @JvmStatic
    fun clear() {
        fontCache.clear()
    }
}