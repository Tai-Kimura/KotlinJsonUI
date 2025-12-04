package com.kotlinjsonui.core

import androidx.compose.ui.graphics.Color
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ConfigurationTest {

    @Before
    fun setUp() {
        // Reset any custom handlers before each test
        Configuration.colorParser = null
        Configuration.fallbackComponent = null
        Configuration.customComponentHandler = null
        Configuration.showErrorsInDebug = true
    }

    @Test
    fun `Colors object has correct default values`() {
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
    fun `Colors shadow has correct alpha`() {
        val shadow = Configuration.Colors.shadow
        assertEquals(0.1f, shadow.alpha, 0.01f)
    }

    @Test
    fun `linkColor is customizable`() {
        val originalLinkColor = Configuration.Colors.linkColor
        assertEquals(Color(0xFF0000EE), originalLinkColor)

        Configuration.Colors.linkColor = Color.Red
        assertEquals(Color.Red, Configuration.Colors.linkColor)

        // Reset
        Configuration.Colors.linkColor = Color(0xFF0000EE)
    }

    @Test
    fun `Sizes object has correct default values`() {
        assertEquals(14, Configuration.Sizes.fontSize)
        assertEquals(8, Configuration.Sizes.cornerRadius)
        assertEquals(16, Configuration.Sizes.padding)
        assertEquals(8, Configuration.Sizes.spacing)
        assertEquals(4f, Configuration.Sizes.shadowElevation, 0.01f)
    }

    @Test
    fun `Animation object has correct default values`() {
        assertEquals(300, Configuration.Animation.duration)
    }

    @Test
    fun `TextField defaults are configured correctly`() {
        assertEquals(Configuration.Colors.background, Configuration.TextField.defaultBackgroundColor)
        assertEquals(Configuration.Colors.text, Configuration.TextField.defaultTextColor)
        assertEquals(Configuration.Colors.placeholder, Configuration.TextField.defaultPlaceholderColor)
        assertEquals(Configuration.Colors.border, Configuration.TextField.defaultBorderColor)
        assertEquals(44, Configuration.TextField.defaultHeight)
        assertEquals(Configuration.Sizes.fontSize, Configuration.TextField.defaultFontSize)
        assertEquals(Configuration.Sizes.cornerRadius, Configuration.TextField.defaultCornerRadius)
    }

    @Test
    fun `Button defaults are configured correctly`() {
        assertEquals(Configuration.Colors.primary, Configuration.Button.defaultBackgroundColor)
        assertEquals(Color.White, Configuration.Button.defaultTextColor)
        assertEquals(Configuration.Colors.disabled, Configuration.Button.defaultDisabledBackgroundColor)
        assertEquals(Color.Gray, Configuration.Button.defaultDisabledTextColor)
        assertEquals(44, Configuration.Button.defaultHeight)
        assertEquals(Configuration.Sizes.fontSize, Configuration.Button.defaultFontSize)
        assertEquals(Configuration.Sizes.cornerRadius, Configuration.Button.defaultCornerRadius)
    }

    @Test
    fun `Switch defaults are configured correctly`() {
        assertEquals(Configuration.Colors.secondary, Configuration.Switch.defaultOnColor)
        assertEquals(Configuration.Colors.disabled, Configuration.Switch.defaultOffColor)
        assertEquals(Color.White, Configuration.Switch.defaultThumbColor)
    }

    @Test
    fun `SelectBox defaults are configured correctly`() {
        assertEquals(Configuration.Colors.background, Configuration.SelectBox.defaultBackgroundColor)
        assertEquals(Configuration.Colors.text, Configuration.SelectBox.defaultTextColor)
        assertEquals(Configuration.Colors.placeholder, Configuration.SelectBox.defaultPlaceholderColor)
        assertEquals(Configuration.Colors.border, Configuration.SelectBox.defaultBorderColor)
        assertEquals(44, Configuration.SelectBox.defaultHeight)
        assertEquals(Configuration.Sizes.cornerRadius, Configuration.SelectBox.defaultCornerRadius)
    }

    @Test
    fun `SelectBox SheetButton defaults are configured correctly`() {
        assertEquals(Configuration.Colors.primary, Configuration.SelectBox.SheetButton.defaultCancelButtonTextColor)
        assertEquals(17, Configuration.SelectBox.SheetButton.defaultFontSize)
        assertEquals(700, Configuration.SelectBox.SheetButton.defaultFontWeight)
    }

    @Test
    fun `Slider defaults are configured correctly`() {
        assertEquals(Configuration.Colors.primary, Configuration.Slider.defaultActiveColor)
        assertEquals(Configuration.Colors.disabled, Configuration.Slider.defaultInactiveColor)
        assertEquals(Color.White, Configuration.Slider.defaultThumbColor)
    }

    @Test
    fun `DatePicker defaults are configured correctly`() {
        assertEquals(Configuration.Colors.background, Configuration.DatePicker.defaultBackgroundColor)
        assertEquals(Configuration.Colors.text, Configuration.DatePicker.defaultTextColor)
        assertEquals(Configuration.Colors.primary, Configuration.DatePicker.defaultSelectedColor)
        assertEquals(Configuration.Colors.secondary, Configuration.DatePicker.defaultTodayColor)
        assertEquals(44, Configuration.DatePicker.defaultHeight)
    }

    @Test
    fun `DatePicker SheetButton defaults are configured correctly`() {
        assertEquals(Configuration.Colors.primary, Configuration.DatePicker.SheetButton.defaultButtonBackgroundColor)
        assertEquals(Color.White, Configuration.DatePicker.SheetButton.defaultButtonTextColor)
        assertEquals(Color.Transparent, Configuration.DatePicker.SheetButton.defaultCancelButtonBackgroundColor)
        assertEquals(Configuration.Colors.primary, Configuration.DatePicker.SheetButton.defaultCancelButtonTextColor)
        assertEquals(17, Configuration.DatePicker.SheetButton.defaultFontSize)
        assertEquals(700, Configuration.DatePicker.SheetButton.defaultFontWeight)
    }

    @Test
    fun `Segment defaults are customizable`() {
        // Test defaults
        assertEquals(Color.White, Configuration.Segment.defaultBackgroundColor)
        assertEquals(Color.Black, Configuration.Segment.defaultTextColor)
        assertEquals(Configuration.Colors.primary, Configuration.Segment.defaultSelectedBackgroundColor)
        assertEquals(Color.White, Configuration.Segment.defaultSelectedTextColor)
        assertEquals(Configuration.Colors.border, Configuration.Segment.defaultBorderColor)
        assertEquals(Configuration.Sizes.cornerRadius, Configuration.Segment.defaultCornerRadius)
        assertEquals(44, Configuration.Segment.defaultHeight)

        // Test customization
        Configuration.Segment.defaultBackgroundColor = Color.Gray
        assertEquals(Color.Gray, Configuration.Segment.defaultBackgroundColor)

        // Reset
        Configuration.Segment.defaultBackgroundColor = Color.White
    }

    @Test
    fun `showErrorsInDebug is configurable`() {
        assertTrue(Configuration.showErrorsInDebug)

        Configuration.showErrorsInDebug = false
        assertFalse(Configuration.showErrorsInDebug)

        Configuration.showErrorsInDebug = true
        assertTrue(Configuration.showErrorsInDebug)
    }

    @Test
    fun `colorParser is initially null`() {
        assertNull(Configuration.colorParser)
    }

    @Test
    fun `fallbackComponent is initially null`() {
        assertNull(Configuration.fallbackComponent)
    }

    @Test
    fun `customComponentHandler is initially null`() {
        assertNull(Configuration.customComponentHandler)
    }
}
