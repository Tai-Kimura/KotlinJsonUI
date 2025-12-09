package com.kotlinjsonui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject

/**
 * Configuration object for default values used across KotlinJsonUI components
 */
object Configuration {
    
    /**
     * Custom color parser function
     * If set, this function will be used to parse colors from JSON instead of the default parser
     * The function receives the JsonObject and key
     * The function should return null if it cannot parse the color
     */
    var colorParser: ((JsonObject, String) -> Color?)? = null
    
    /**
     * Whether to show error components in debug mode
     * When true, components that fail to render will show an error message
     * When false, errors are silently logged
     */
    var showErrorsInDebug: Boolean = true
    
    /**
     * Custom fallback component to render when an unknown component type is encountered
     * The function receives the JsonObject and data map
     */
    var fallbackComponent: (@Composable (JsonObject, Map<String, Any>) -> Unit)? = null
    
    /**
     * Custom component handler to render app-specific custom components
     * The function receives the component type, JsonObject and data map
     * Should return true if the component was handled, false otherwise
     */
    var customComponentHandler: (@Composable (String, JsonObject, Map<String, Any>) -> Boolean)? = null
    // Global color defaults (all mutable for app customization)
    object Colors {
        var background = Color.White
        var text = Color.Black
        var placeholder = Color.Gray
        var border = Color.LightGray
        var primary = Color(0xFF007AFF)
        var secondary = Color(0xFF34C759)
        var error = Color(0xFFFF3B30)
        var disabled = Color(0xFFC7C7CC)
        var shadow = Color.Black.copy(alpha = 0.1f)
        var linkColor = Color(0xFF0000EE)  // Default blue color for links
    }
    
    // Global size defaults
    object Sizes {
        const val fontSize = 14
        const val cornerRadius = 8
        const val padding = 16
        const val spacing = 8
        const val shadowElevation = 4f
    }
    
    // Global animation defaults
    object Animation {
        const val duration = 300
    }
    
    // TextField specific defaults (mutable for app customization)
    object TextField {
        var defaultBackgroundColor = Colors.background
        var defaultHighlightBackgroundColor = Colors.background
        var defaultTextColor = Colors.text
        var defaultPlaceholderColor = Colors.placeholder
        var defaultBorderColor = Colors.border
        const val defaultHeight = 44
        const val defaultFontSize = Sizes.fontSize
        const val defaultCornerRadius = Sizes.cornerRadius
    }
    
    // Button specific defaults
    object Button {
        val defaultBackgroundColor = Colors.primary
        val defaultTextColor = Color.White
        val defaultDisabledBackgroundColor = Colors.disabled
        val defaultDisabledTextColor = Color.Gray
        const val defaultHeight = 44
        const val defaultFontSize = Sizes.fontSize
        const val defaultCornerRadius = Sizes.cornerRadius
    }
    
    // Switch specific defaults
    object Switch {
        val defaultOnColor = Colors.secondary
        val defaultOffColor = Colors.disabled
        val defaultThumbColor = Color.White
    }
    
    // SelectBox specific defaults
    object SelectBox {
        val defaultBackgroundColor = Colors.background
        val defaultTextColor = Colors.text
        val defaultPlaceholderColor = Colors.placeholder
        val defaultBorderColor = Colors.border
        val defaultSheetBackgroundColor = Colors.background  // 白
        val defaultSheetTextColor = Colors.text  // 黒
        const val defaultHeight = 44
        const val defaultCornerRadius = Sizes.cornerRadius
        
        // Sheet button styles
        object SheetButton {
            val defaultCancelButtonTextColor = Colors.primary
            const val defaultFontSize = 17  // sp
            const val defaultFontWeight = 700  // Bold
        }
    }
    
    // Slider specific defaults
    object Slider {
        val defaultActiveColor = Colors.primary
        val defaultInactiveColor = Colors.disabled
        val defaultThumbColor = Color.White
    }
    
    // DatePicker specific defaults
    object DatePicker {
        val defaultBackgroundColor = Colors.background
        val defaultTextColor = Colors.text
        val defaultSelectedColor = Colors.primary
        val defaultTodayColor = Colors.secondary
        val defaultSheetBackgroundColor = Colors.background  // 白
        val defaultSheetTextColor = Colors.text  // 黒
        const val defaultHeight = 44
        
        // Sheet button styles
        object SheetButton {
            val defaultButtonBackgroundColor = Colors.primary
            val defaultButtonTextColor = Color.White
            val defaultCancelButtonBackgroundColor = Color.Transparent
            val defaultCancelButtonTextColor = Colors.primary
            const val defaultFontSize = 17  // sp
            const val defaultFontWeight = 700  // Bold
        }
    }
    
    // Segment (TabLayout) specific defaults
    object Segment {
        var defaultBackgroundColor = Color.White
        var defaultTextColor = Color.Black
        var defaultSelectedBackgroundColor = Colors.primary
        var defaultSelectedTextColor = Color.White
        var defaultBorderColor = Colors.border
        const val defaultCornerRadius = Sizes.cornerRadius
        const val defaultHeight = 44
    }
}