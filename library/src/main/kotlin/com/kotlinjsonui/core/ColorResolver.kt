package com.kotlinjsonui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Resolves a color name string to a Compose Color at runtime.
 * Checks Android color resources first, then hex color parsing.
 */
object ColorResolver {
    @Composable
    fun resolve(name: String): Color {
        val context = LocalContext.current
        // 1. Try Android color resource (e.g., "gold" -> R.color.gold)
        try {
            val resId = context.resources.getIdentifier(name, "color", context.packageName)
            if (resId != 0) {
                return Color(ContextCompat.getColor(context, resId))
            }
        } catch (_: Exception) {}

        // 2. Try hex color (e.g., "#FFD700")
        try {
            return Color(android.graphics.Color.parseColor(name))
        } catch (_: Exception) {}

        return Color.Unspecified
    }
}
