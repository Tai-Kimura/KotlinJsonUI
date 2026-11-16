package com.kotlinjsonui.dynamic

import androidx.compose.runtime.compositionLocalOf

/**
 * Configuration for safe area insets behavior
 * Used to communicate from parent components (like TabView) to child components (like SafeAreaView)
 * which edges should be ignored for safe area padding
 *
 * NOTE: Lives in the main `kotlinjsonui` module (not `kotlinjsonui-dynamic`) so that
 * generated static Compose views can reference it in Release builds where the dynamic
 * module is `debugImplementation`-only. The `com.kotlinjsonui.dynamic` package name is
 * preserved for source compatibility with existing generated code and hand-written
 * consumers.
 */
data class SafeAreaConfig(
    val ignoreTop: Boolean = false,
    val ignoreBottom: Boolean = false,
    val ignoreLeft: Boolean = false,
    val ignoreRight: Boolean = false
)

/**
 * CompositionLocal to provide SafeAreaConfig to child composables
 * Default is to not ignore any edges
 */
val LocalSafeAreaConfig = compositionLocalOf { SafeAreaConfig() }
