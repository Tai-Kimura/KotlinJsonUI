package com.kotlinjsonui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.google.gson.JsonObject
import java.util.UUID

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
     * Theme-aware color parser. Called by Dynamic-mode color resolution
     * BEFORE the existing `colorParser` + ResourceCache + hex fallback.
     * Apps wire the tool-generated `ColorManager.compose.color(key)` here so
     * layout color keys follow the active mode without touching every
     * Composable.
     *
     * Closure args: `(currentThemeMode, key)` → `Color?`. Return `null` to
     * fall through to the remaining resolution chain.
     *
     * Wiring example:
     * ```
     * Configuration.themedColorParser = { _, key -> ColorManager.compose.color(key) }
     * // (ColorManager reads its own currentMode internally, so the mode arg
     * //  can be ignored if you let ColorManager own the state.)
     * ```
     */
    var themedColorParser: ((mode: String, key: String) -> Color?)? = null

    /**
     * Current theme mode as a raw string label (e.g. `"light"`, `"dark"`,
     * `"high_contrast"`). Backed by `mutableStateOf` so Composables reading
     * `currentThemeMode.value` auto-recompose on `setThemeMode(...)`.
     *
     * Purely a label — actual color values live in `themedColorParser`.
     */
    private val _currentThemeMode: MutableState<String> = mutableStateOf("light")

    val currentThemeMode: String
        get() = _currentThemeMode.value

    /**
     * Switch the active theme mode. Updates the `mutableStateOf`-backed
     * value (triggering Compose recomposition for every reader) AND fires
     * registered callbacks for non-Compose observers (Views, legacy code).
     * No-op when `mode` already matches.
     */
    fun setThemeMode(mode: String) {
        if (_currentThemeMode.value == mode) return
        _currentThemeMode.value = mode
        themeChangeCallbacks.values.forEach { it.invoke(mode) }
    }

    private val themeChangeCallbacks: MutableMap<UUID, (String) -> Unit> = mutableMapOf()

    /**
     * Subscribe to theme-mode changes from non-Compose code. Returns a
     * closure that unsubscribes when invoked.
     */
    fun subscribeToThemeChanges(callback: (String) -> Unit): () -> Unit {
        val id = UUID.randomUUID()
        themeChangeCallbacks[id] = callback
        return { themeChangeCallbacks.remove(id) }
    }
    
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
    
    // Global font defaults (all mutable for app customization)
    object Font {
        /** Default font family (null uses system default) */
        var family: FontFamily? = null

        /** Default font weight */
        var weight: FontWeight = FontWeight.Normal

        /** Default font size in sp */
        var size: Int = 14

        /**
         * Custom font provider function.
         * Receives font name string from JSON, returns FontFamily or null.
         * Use this to map font names to custom FontFamily instances.
         */
        var fontProvider: ((String) -> FontFamily?)? = null

        /** Resolve a font name to FontFamily using fontProvider, then family fallback */
        fun resolve(name: String?): FontFamily? {
            if (name != null) {
                fontProvider?.invoke(name)?.let { return it }
            }
            return family
        }
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