package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.TextUnit
import com.google.gson.JsonObject
import com.kotlinjsonui.core.jsonUILetterSpacing
import com.kotlinjsonui.core.jsonUILineHeight

/** The text style's letter spacing drawn by the component's fontScaling / maxFontScale (jsonUILetterSpacing). */
@Composable
@ReadOnlyComposable
internal fun JsonObject.letterSpaced(): TextUnit = jsonUILetterSpacing(fontScalingAttr(), maxFontScaleAttr())

/**
 * The text style's line height drawn by the component's fontScaling / maxFontScale (jsonUILineHeight): a text that
 * does not follow keeps its line box at every text size (ticket kjui-theme-line-height-scales-under-font-scaling-false).
 */
@Composable
@ReadOnlyComposable
internal fun JsonObject.lineHeighted(fontSize: TextUnit = TextUnit.Unspecified, lineHeight: TextUnit = TextUnit.Unspecified): TextUnit =
    jsonUILineHeight(fontSize, fontScalingAttr(), maxFontScaleAttr(), lineHeight)
