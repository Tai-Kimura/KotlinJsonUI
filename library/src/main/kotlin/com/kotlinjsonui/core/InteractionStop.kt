package com.kotlinjsonui.core

import androidx.compose.runtime.compositionLocalOf

/**
 * True inside a node whose `userInteractionEnabled` is false, or a binding
 * that is false.
 *
 * The flag stops a node and everything in it. The tap rule (jsonui-cli
 * shared/core/tap_accessibility.rb) says no tap — no click, no Role.Button —
 * for what a stopping node holds; what it draws in a composable of its own (a
 * Collection's cells, an Embed's screen, a TabView tab's view) the build's
 * annotation cannot reach, so the stop is handed down at run time: the
 * stopping node provides this local, and the drawn composable's clicks read
 * it. Both render paths use this one local — the code `kjui build` emits and
 * the Dynamic runtime (DynamicView).
 */
val LocalInteractionStopped = compositionLocalOf { false }
