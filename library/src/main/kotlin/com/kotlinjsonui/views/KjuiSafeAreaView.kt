package com.kotlinjsonui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.kotlinjsonui.R

/**
 * A FrameLayout that automatically applies padding for system UI insets (status bar, navigation bar, etc.)
 * Perfect for edge-to-edge layouts
 * Requires minSdk 24
 */
class KjuiSafeAreaView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    
    // Flags for which insets to apply
    private var applyTopInset = true
    private var applyBottomInset = true
    private var applyLeftInset = true
    private var applyRightInset = true
    private var applyStartInset = true
    private var applyEndInset = true
    
    // Store original padding
    private var originalPaddingTop = 0
    private var originalPaddingBottom = 0
    private var originalPaddingLeft = 0
    private var originalPaddingRight = 0
    private var originalPaddingStart = 0
    private var originalPaddingEnd = 0
    
    // Store the inset positions from attributes
    private var safeAreaInsetPositions: Set<String> = setOf("top", "bottom", "left", "right")
    
    // Content inset adjustment behavior
    private var contentInsetAdjustmentBehavior: String = "always"
    
    // Flag to track if we should handle keyboard
    private var handleKeyboard: Boolean = true
    
    init {
        // Store original padding
        originalPaddingTop = paddingTop
        originalPaddingBottom = paddingBottom
        originalPaddingLeft = paddingLeft
        originalPaddingRight = paddingRight
        originalPaddingStart = paddingStart
        originalPaddingEnd = paddingEnd
        
        // Process custom attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KjuiSafeAreaView, defStyleAttr, 0)
            try {
                // Get inset positions string
                val positionsString = typedArray.getString(R.styleable.KjuiSafeAreaView_safeAreaInsetPositions)
                if (!positionsString.isNullOrEmpty()) {
                    parseSafeAreaInsetPositions(positionsString)
                }
                
                // Content inset adjustment behavior
                val behavior = typedArray.getString(R.styleable.KjuiSafeAreaView_contentInsetAdjustmentBehavior)
                if (!behavior.isNullOrEmpty()) {
                    contentInsetAdjustmentBehavior = behavior.lowercase()
                    handleKeyboard = (contentInsetAdjustmentBehavior == "always")
                }
                
                // Individual inset flags (for backwards compatibility)
                applyTopInset = typedArray.getBoolean(R.styleable.KjuiSafeAreaView_applyTopInset, applyTopInset)
                applyBottomInset = typedArray.getBoolean(R.styleable.KjuiSafeAreaView_applyBottomInset, applyBottomInset)
                applyLeftInset = typedArray.getBoolean(R.styleable.KjuiSafeAreaView_applyLeftInset, applyLeftInset)
                applyRightInset = typedArray.getBoolean(R.styleable.KjuiSafeAreaView_applyRightInset, applyRightInset)
                applyStartInset = typedArray.getBoolean(R.styleable.KjuiSafeAreaView_applyStartInset, applyStartInset)
                applyEndInset = typedArray.getBoolean(R.styleable.KjuiSafeAreaView_applyEndInset, applyEndInset)
            } finally {
                typedArray.recycle()
            }
        }
        
        // Set up window insets listener
        setupWindowInsets()
    }
    
    private fun parseSafeAreaInsetPositions(positionsString: String) {
        // Reset all flags first
        applyTopInset = false
        applyBottomInset = false
        applyLeftInset = false
        applyRightInset = false
        applyStartInset = false
        applyEndInset = false
        
        // Parse the positions string (can be pipe-separated or comma-separated)
        val positions = positionsString.split("|", ",").map { it.trim().lowercase() }.toSet()
        safeAreaInsetPositions = positions
        
        // Apply the specified positions
        for (position in positions) {
            when (position) {
                "top" -> applyTopInset = true
                "bottom" -> applyBottomInset = true
                "left" -> applyLeftInset = true
                "right" -> applyRightInset = true
                "start", "leading" -> applyStartInset = true
                "end", "trailing" -> applyEndInset = true
                "vertical" -> {
                    applyTopInset = true
                    applyBottomInset = true
                }
                "horizontal" -> {
                    applyLeftInset = true
                    applyRightInset = true
                    applyStartInset = true
                    applyEndInset = true
                }
                "all" -> {
                    applyTopInset = true
                    applyBottomInset = true
                    applyLeftInset = true
                    applyRightInset = true
                    applyStartInset = true
                    applyEndInset = true
                }
            }
        }
    }
    
    private fun setupWindowInsets() {
        // Request to be laid out when window insets change
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
            applyWindowInsets(windowInsets)
            windowInsets
        }
        
        // Request window insets
        requestApplyInsets()
    }
    
    private fun applyWindowInsets(windowInsets: WindowInsetsCompat) {
        // Skip if behavior is "never"
        if (contentInsetAdjustmentBehavior == "never") {
            setPadding(originalPaddingLeft, originalPaddingTop, originalPaddingRight, originalPaddingBottom)
            return
        }
        
        // Get system bars and display cutout insets
        var insetTypes = WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
        
        // Add keyboard insets if behavior is "always"
        if (handleKeyboard && contentInsetAdjustmentBehavior == "always") {
            insetTypes = insetTypes or WindowInsetsCompat.Type.ime()
        }
        
        val insets = windowInsets.getInsets(insetTypes)
        
        val isRtl = layoutDirection == View.LAYOUT_DIRECTION_RTL
        
        // Calculate padding based on enabled insets
        val paddingTop = originalPaddingTop + if (applyTopInset) insets.top else 0
        val paddingBottom = originalPaddingBottom + if (applyBottomInset) insets.bottom else 0
        
        // Handle RTL layout
        val paddingLeft = if (isRtl) {
            originalPaddingLeft + if (applyEndInset || applyRightInset) insets.right else 0
        } else {
            originalPaddingLeft + if (applyStartInset || applyLeftInset) insets.left else 0
        }
        
        val paddingRight = if (isRtl) {
            originalPaddingRight + if (applyStartInset || applyLeftInset) insets.left else 0
        } else {
            originalPaddingRight + if (applyEndInset || applyRightInset) insets.right else 0
        }
        
        // Apply the calculated padding
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }
    
    /**
     * Set content inset adjustment behavior
     * @param behavior "always" (includes keyboard) or "never" (no insets)
     */
    fun setContentInsetAdjustmentBehavior(behavior: String) {
        contentInsetAdjustmentBehavior = behavior.lowercase()
        handleKeyboard = (contentInsetAdjustmentBehavior == "always")
        requestApplyInsets()
    }
    
    /**
     * Set which insets to apply programmatically
     */
    fun setSafeAreaInsetPositions(positions: List<String>) {
        parseSafeAreaInsetPositions(positions.joinToString(","))
        requestApplyInsets()
    }
    
    /**
     * Set which insets to apply programmatically using varargs
     */
    fun setSafeAreaInsetPositions(vararg positions: String) {
        parseSafeAreaInsetPositions(positions.joinToString(","))
        requestApplyInsets()
    }
    
    /**
     * Enable or disable specific insets
     */
    fun setInsetEnabled(position: String, enabled: Boolean) {
        when (position.lowercase()) {
            "top" -> applyTopInset = enabled
            "bottom" -> applyBottomInset = enabled
            "left" -> applyLeftInset = enabled
            "right" -> applyRightInset = enabled
            "start", "leading" -> applyStartInset = enabled
            "end", "trailing" -> applyEndInset = enabled
            "vertical" -> {
                applyTopInset = enabled
                applyBottomInset = enabled
            }
            "horizontal" -> {
                applyLeftInset = enabled
                applyRightInset = enabled
                applyStartInset = enabled
                applyEndInset = enabled
            }
        }
        requestApplyInsets()
    }
    
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Re-request insets when attached to window
        requestApplyInsets()
    }
}