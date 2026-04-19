package com.kotlinjsonui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlinjsonui.BuildConfig
import com.kotlinjsonui.core.DynamicModeManager

/**
 * A composable toggle switch for Dynamic Mode
 * Only visible in DEBUG builds
 */
@Composable
fun DynamicModeToggle(
    modifier: Modifier = Modifier,
    onToggle: ((Boolean) -> Unit)? = null
) {
    if (!BuildConfig.DEBUG) {
        // Don't show anything in release builds
        return
    }
    
    val context = LocalContext.current
    
    // Initialize if needed
    if (!DynamicModeManager.isInitialized) {
        DynamicModeManager.initialize(context)
    }
    
    val isEnabled by DynamicModeManager.isDynamicModeEnabled.collectAsState()
    val isAvailable by DynamicModeManager.isDynamicModeAvailable.collectAsState()
    
    if (isAvailable) {
        val context = LocalContext.current
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dynamic Mode")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isEnabled,
                onCheckedChange = { checked ->
                    DynamicModeManager.setDynamicModeEnabled(context, checked)
                    onToggle?.invoke(checked)
                }
            )
        }
    }
}