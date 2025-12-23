package com.kotlinjsonui.core

import android.content.Context
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.kotlinjsonui.BuildConfig
import kotlinx.coroutines.launch

/**
 * Interface for defining screens in the developer menu
 */
interface DeveloperScreen {
    val name: String
}

/**
 * Container composable that wraps content with developer menu gestures
 *
 * Features (DEBUG builds only):
 * - Double tap to show view selector
 * - Long press to toggle dynamic mode
 *
 * In RELEASE builds, just displays content without any developer features.
 *
 * @param currentScreen The currently selected screen
 * @param screens List of available screens
 * @param onScreenChange Callback when a screen is selected
 * @param modifier Modifier for the container
 * @param content Content builder that receives the current screen
 */
@Composable
fun <T : DeveloperScreen> DeveloperMenuContainer(
    currentScreen: T,
    screens: List<T>,
    onScreenChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) {
    // In RELEASE builds, just show content without any developer features
    if (!BuildConfig.DEBUG) {
        content(currentScreen)
        return
    }

    // DEBUG: Enable developer menu features
    val context = LocalContext.current
    val isDynamicModeEnabled by DynamicModeManager.isDynamicModeEnabled.collectAsState()

    var showViewSelector by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        showViewSelector = true
                    },
                    onLongPress = {
                        val newMode = !isDynamicModeEnabled
                        DynamicModeManager.setDynamicModeEnabled(context, newMode)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = if (newMode) "Dynamic Mode: ON" else "Dynamic Mode: OFF",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            key(isDynamicModeEnabled) {
                content(currentScreen)
            }

            if (showViewSelector) {
                DeveloperMenuDialog(
                    currentScreen = currentScreen,
                    screens = screens,
                    isDynamicModeEnabled = isDynamicModeEnabled,
                    onScreenSelected = { screen ->
                        onScreenChange(screen)
                        showViewSelector = false
                    },
                    onDynamicModeToggle = {
                        val newMode = !isDynamicModeEnabled
                        DynamicModeManager.setDynamicModeEnabled(context, newMode)
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = if (newMode) "Dynamic Mode: ON" else "Dynamic Mode: OFF",
                                duration = SnackbarDuration.Short
                            )
                        }
                    },
                    onDismiss = { showViewSelector = false }
                )
            }
        }
    }
}

/**
 * Dialog for the developer menu (only functional in DEBUG builds)
 */
@Composable
fun <T : DeveloperScreen> DeveloperMenuDialog(
    currentScreen: T,
    screens: List<T>,
    isDynamicModeEnabled: Boolean,
    onScreenSelected: (T) -> Unit,
    onDynamicModeToggle: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Developer Menu") },
        text = {
            Column {
                // Dynamic Mode Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Dynamic Mode")
                    Switch(
                        checked = isDynamicModeEnabled,
                        onCheckedChange = { onDynamicModeToggle() }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    "Select View:",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // View selection buttons
                screens.forEach { screen ->
                    TextButton(
                        onClick = { onScreenSelected(screen) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = if (screen == currentScreen) "● ${screen.name}" else "○ ${screen.name}",
                                color = if (screen == currentScreen)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
