package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration

/**
 * Helper class for parsing colors from JSON
 * Handles hex color strings and provides safe parsing with fallback values
 * Uses Configuration.colorParser if available, otherwise uses default parsing
 */
object ColorParser {
    
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
     * Supports hex color strings like "#FFFFFF" or "FFFFFF"
     * @param colorString The color string to parse
     */
    fun parseColorString(
        colorString: String?
    ): Color? {
        if (colorString == null) return null
        
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