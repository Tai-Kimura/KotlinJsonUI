package com.kotlinjsonui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.isSpecified

/**
 * The `tintColor` a container hands down to the controls inside it.
 *
 * `tintColor` is the accent of a node's operable parts — a control's accent
 * (a Switch's track, a CheckBox's box, a Slider's thumb and filled track, a
 * Progress indicator, a Radio's selected glyph, a Segment's or a TabView's selected
 * item) and a text field's cursor — never the text colour (jsonui-cli 1.9.0
 * ruling). On iOS `.tint` on a container reaches the controls inside it; this
 * local is the same on Compose. A node with children (or one that draws a
 * layout of its own — a Collection's cells, an Embed's screen, a TabView's
 * tabs) and a `tintColor` provides it around what it composes; a control reads
 * its own `tintColor` first and this local second ([jsonUITintOr]).
 *
 * It is the project's own local, not `LocalContentColor`: that one is the text
 * and icon colour, which a tint must not change. Both render paths use this
 * one local — the code `kjui build` emits and the Dynamic runtime — so a tint
 * crosses an included layout, a cell and an embedded screen on either path.
 *
 * [Color.Unspecified] (the default) is "no tint handed down".
 */
val LocalJsonUITint = compositionLocalOf { Color.Unspecified }

/**
 * The accent a control draws with when it declares none of its own: the tint
 * the nearest container handed down ([LocalJsonUITint]), else [fallback] (the
 * control's own default — for the Material controls, the theme's primary).
 */
@Composable
@ReadOnlyComposable
fun jsonUITintOr(fallback: Color): Color = tintOr(LocalJsonUITint.current, fallback)

/** The tint handed down, or null when none is (for a parameter whose null means "the default"). */
@Composable
@ReadOnlyComposable
fun jsonUITintOrNull(): Color? = LocalJsonUITint.current.takeIf { it.isSpecified }

/** [inherited] when a tint was handed down, else [fallback]. */
internal fun tintOr(inherited: Color, fallback: Color): Color =
    if (inherited.isSpecified) inherited else fallback
