package com.kotlinjsonui.core

import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfigurationTest {

    @Test
    fun configuration_colorsDefaults() {
        assertEquals(Color.White, Configuration.Colors.background)
        assertEquals(Color.Black, Configuration.Colors.text)
        assertEquals(Color.Gray, Configuration.Colors.placeholder)
        assertEquals(Color.LightGray, Configuration.Colors.border)
        assertEquals(Color(0xFF007AFF), Configuration.Colors.primary)
        assertEquals(Color(0xFF34C759), Configuration.Colors.secondary)
        assertEquals(Color(0xFFFF3B30), Configuration.Colors.error)
        assertEquals(Color(0xFFC7C7CC), Configuration.Colors.disabled)
    }

    @Test
    fun configuration_sizesDefaults() {
        assertEquals(14, Configuration.Sizes.fontSize)
        assertEquals(8, Configuration.Sizes.cornerRadius)
        assertEquals(16, Configuration.Sizes.padding)
        assertEquals(8, Configuration.Sizes.spacing)
        assertEquals(4f, Configuration.Sizes.shadowElevation)
    }

    @Test
    fun configuration_animationDefaults() {
        assertEquals(300, Configuration.Animation.duration)
    }

    @Test
    fun configuration_textFieldDefaults() {
        assertEquals(Configuration.Colors.background, Configuration.TextField.defaultBackgroundColor)
        assertEquals(Configuration.Colors.text, Configuration.TextField.defaultTextColor)
        assertEquals(Configuration.Colors.placeholder, Configuration.TextField.defaultPlaceholderColor)
        assertEquals(44, Configuration.TextField.defaultHeight)
        assertEquals(14, Configuration.TextField.defaultFontSize)
        assertEquals(8, Configuration.TextField.defaultCornerRadius)
    }

    @Test
    fun configuration_buttonDefaults() {
        assertEquals(Configuration.Colors.primary, Configuration.Button.defaultBackgroundColor)
        assertEquals(Color.White, Configuration.Button.defaultTextColor)
        assertEquals(Configuration.Colors.disabled, Configuration.Button.defaultDisabledBackgroundColor)
        assertEquals(44, Configuration.Button.defaultHeight)
        assertEquals(14, Configuration.Button.defaultFontSize)
        assertEquals(8, Configuration.Button.defaultCornerRadius)
    }

    @Test
    fun configuration_switchDefaults() {
        assertEquals(Configuration.Colors.secondary, Configuration.Switch.defaultOnColor)
        assertEquals(Configuration.Colors.disabled, Configuration.Switch.defaultOffColor)
        assertEquals(Color.White, Configuration.Switch.defaultThumbColor)
    }

    @Test
    fun configuration_selectBoxDefaults() {
        assertEquals(Configuration.Colors.background, Configuration.SelectBox.defaultBackgroundColor)
        assertEquals(Configuration.Colors.text, Configuration.SelectBox.defaultTextColor)
        assertEquals(44, Configuration.SelectBox.defaultHeight)
        assertEquals(8, Configuration.SelectBox.defaultCornerRadius)
    }

    @Test
    fun configuration_sliderDefaults() {
        assertEquals(Configuration.Colors.primary, Configuration.Slider.defaultActiveColor)
        assertEquals(Configuration.Colors.disabled, Configuration.Slider.defaultInactiveColor)
        assertEquals(Color.White, Configuration.Slider.defaultThumbColor)
    }

    @Test
    fun configuration_datePickerDefaults() {
        assertEquals(Configuration.Colors.background, Configuration.DatePicker.defaultBackgroundColor)
        assertEquals(Configuration.Colors.text, Configuration.DatePicker.defaultTextColor)
        assertEquals(Configuration.Colors.primary, Configuration.DatePicker.defaultSelectedColor)
        assertEquals(44, Configuration.DatePicker.defaultHeight)
    }

    @Test
    fun configuration_segmentDefaults() {
        assertEquals(Color.White, Configuration.Segment.defaultBackgroundColor)
        assertEquals(Color.Black, Configuration.Segment.defaultTextColor)
        assertEquals(Configuration.Colors.primary, Configuration.Segment.defaultSelectedBackgroundColor)
        assertEquals(Color.White, Configuration.Segment.defaultSelectedTextColor)
        assertEquals(8, Configuration.Segment.defaultCornerRadius)
        assertEquals(44, Configuration.Segment.defaultHeight)
    }

    @Test
    fun configuration_linkColor() {
        assertNotNull(Configuration.Colors.linkColor)
    }

    @Test
    fun configuration_colorParserDefault() {
        assertNull(Configuration.colorParser)
    }

    @Test
    fun configuration_showErrorsInDebug() {
        assertTrue(Configuration.showErrorsInDebug)
    }

    @Test
    fun configuration_fallbackComponentDefault() {
        assertNull(Configuration.fallbackComponent)
    }

    @Test
    fun configuration_customComponentHandlerDefault() {
        assertNull(Configuration.customComponentHandler)
    }

    @Test
    fun configuration_selectBoxSheetButtonDefaults() {
        assertEquals(Configuration.Colors.primary, Configuration.SelectBox.SheetButton.defaultCancelButtonTextColor)
        assertEquals(17, Configuration.SelectBox.SheetButton.defaultFontSize)
        assertEquals(700, Configuration.SelectBox.SheetButton.defaultFontWeight)
    }

    @Test
    fun configuration_datePickerSheetButtonDefaults() {
        assertEquals(Configuration.Colors.primary, Configuration.DatePicker.SheetButton.defaultButtonBackgroundColor)
        assertEquals(Color.White, Configuration.DatePicker.SheetButton.defaultButtonTextColor)
        assertEquals(17, Configuration.DatePicker.SheetButton.defaultFontSize)
        assertEquals(700, Configuration.DatePicker.SheetButton.defaultFontWeight)
    }
}
