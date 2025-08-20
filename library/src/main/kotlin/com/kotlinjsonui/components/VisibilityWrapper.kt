package com.kotlinjsonui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha

/**
 * A wrapper component that handles visibility states for KotlinJsonUI
 * 
 * @param visibility The visibility state ("visible", "invisible", "gone")
 * @param modifier Optional modifier for the wrapper
 * @param content The content to show/hide
 */
@Composable
fun VisibilityWrapper(
    visibility: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    when (visibility.lowercase()) {
        "gone" -> {
            // Don't render anything when gone
        }
        "invisible" -> {
            // Render but make it invisible (keeps layout space)
            androidx.compose.foundation.layout.Box(
                modifier = modifier.alpha(0f)
            ) {
                content()
            }
        }
        else -> {
            // "visible" or any other value defaults to visible
            androidx.compose.foundation.layout.Box(
                modifier = modifier
            ) {
                content()
            }
        }
    }
}

/**
 * A wrapper component that handles boolean hidden state
 * 
 * @param hidden Whether the content should be hidden
 * @param modifier Optional modifier for the wrapper
 * @param content The content to show/hide
 */
@Composable
fun VisibilityWrapper(
    hidden: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (!hidden) {
        androidx.compose.foundation.layout.Box(
            modifier = modifier
        ) {
            content()
        }
    }
}

/**
 * A wrapper component that handles both visibility and hidden states
 * 
 * @param visibility Optional visibility state ("visible", "invisible", "gone")
 * @param hidden Optional hidden boolean state
 * @param modifier Optional modifier for the wrapper
 * @param content The content to show/hide
 */
@Composable
fun VisibilityWrapper(
    visibility: String? = null,
    hidden: Boolean? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Handle hidden first (takes precedence)
    if (hidden == true) {
        // Don't render anything when hidden
        return
    }
    
    // Then handle visibility
    when (visibility?.lowercase()) {
        "gone" -> {
            // Don't render anything when gone
        }
        "invisible" -> {
            // Render but make it invisible (keeps layout space)
            androidx.compose.foundation.layout.Box(
                modifier = modifier.alpha(0f)
            ) {
                content()
            }
        }
        else -> {
            // "visible", null, or any other value defaults to visible
            androidx.compose.foundation.layout.Box(
                modifier = modifier
            ) {
                content()
            }
        }
    }
}