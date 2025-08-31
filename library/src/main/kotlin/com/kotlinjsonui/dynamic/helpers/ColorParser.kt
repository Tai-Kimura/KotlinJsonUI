package com.kotlinjsonui.dynamic.helpers

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.ResourceCache

/**
 * Helper class for parsing colors from JSON
 * Handles hex color strings and provides safe parsing with fallback values
 * Uses ResourceCache for color resolution, then Configuration.colorParser if available,
 * otherwise uses default parsing
 */
object ColorParser {
    
    // Store context for non-composable usage
    private var cachedContext: Context? = null
    
    fun init(context: Context) {
        cachedContext = context
        ResourceCache.init(context)
    }
    
    /**
     * Parse a color from JSON
     * First checks if Configuration.colorParser is set, otherwise uses default parsing
     * @param json The JSON object containing the color
     * @param key The key to look for in the JSON
     */
    fun parseColor(
        json: JsonObject,
        key: String
    ): Color? {
        // Check if Configuration has a custom color parser with the same signature
        Configuration.colorParser?.let { parser ->
            return parser(json, key)
        }
        
        // Fall back to default parsing
        val colorString = json.get(key)?.asString ?: return null
        return parseColorString(colorString)
    }
    
    /**
     * Parse a color from a string value
     * First tries to resolve from ResourceCache, then supports hex color strings like "#FFFFFF" or "FFFFFF"
     * @param colorString The color string to parse
     * @param context Optional context for resource resolution
     */
    fun parseColorString(
        colorString: String?,
        context: Context? = cachedContext
    ): Color? {
        if (colorString == null) return null
        
        // Try to resolve from ResourceCache if context is available
        context?.let { ctx ->
            val resolvedColor = ResourceCache.resolveColor(colorString, ctx)
            if (resolvedColor != null && resolvedColor != colorString) {
                // Color was resolved from cache, parse the hex value
                return try {
                    Color(android.graphics.Color.parseColor(resolvedColor))
                } catch (e: Exception) {
                    null
                }
            }
        }
        
        // Fall back to default parsing
        return try {
            val hexColor = if (colorString.startsWith("#")) {
                colorString
            } else {
                "#$colorString"
            }
            Color(android.graphics.Color.parseColor(hexColor))
        } catch (e: Exception) {
            // Invalid color format
            null
        }
    }
    
    /**
     * Parse multiple colors from JSON
     * Returns a map of color keys to parsed colors
     */
    fun parseColors(
        json: JsonObject,
        vararg keys: String
    ): Map<String, Color?> {
        return keys.associateWith { key ->
            parseColor(json, key)
        }
    }
    
    /**
     * Common color parsing for text-related components
     */
    fun parseTextColors(json: JsonObject): TextColors {
        return TextColors(
            textColor = parseColor(json, "fontColor") 
                ?: parseColor(json, "textColor") 
                ?: parseColor(json, "color"),
            hintColor = parseColor(json, "hintColor") 
                ?: parseColor(json, "placeholderColor"),
            backgroundColor = parseColor(json, "background") 
                ?: parseColor(json, "backgroundColor"),
            borderColor = parseColor(json, "borderColor")
        )
    }
    
    /**
     * Common color parsing for button-like components
     */
    fun parseButtonColors(json: JsonObject): ButtonColors {
        return ButtonColors(
            backgroundColor = parseColor(json, "background") 
                ?: parseColor(json, "backgroundColor"),
            textColor = parseColor(json, "fontColor") 
                ?: parseColor(json, "textColor") 
                ?: parseColor(json, "color"),
            borderColor = parseColor(json, "borderColor"),
            disabledBackgroundColor = parseColor(json, "disabledBackground") 
                ?: parseColor(json, "disabledBackgroundColor"),
            disabledTextColor = parseColor(json, "disabledTextColor")
        )
    }
    
    /**
     * Data class for text-related colors
     */
    data class TextColors(
        val textColor: Color? = null,
        val hintColor: Color? = null,
        val backgroundColor: Color? = null,
        val borderColor: Color? = null
    )
    
    /**
     * Data class for button-related colors
     */
    data class ButtonColors(
        val backgroundColor: Color? = null,
        val textColor: Color? = null,
        val borderColor: Color? = null,
        val disabledBackgroundColor: Color? = null,
        val disabledTextColor: Color? = null
    )
}