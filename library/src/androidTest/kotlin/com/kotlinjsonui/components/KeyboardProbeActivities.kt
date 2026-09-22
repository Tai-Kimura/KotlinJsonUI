package com.kotlinjsonui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

/** The reporting face's window: `adjustNothing` in the manifest, edge-to-edge in code. */
class AdjustNothingEdgeToEdgeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
    }
}

/** The framework-resize window, as the control. */
class AdjustResizeActivity : ComponentActivity()
