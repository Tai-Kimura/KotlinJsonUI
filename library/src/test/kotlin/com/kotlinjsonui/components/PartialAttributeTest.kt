package com.kotlinjsonui.components

import org.junit.Assert.*
import org.junit.Test

class PartialAttributeTest {

    @Test
    fun `PartialAttribute holds correct values`() {
        val attr = PartialAttribute(
            startIndex = 0,
            endIndex = 10,
            fontColor = "#FF0000",
            fontSize = 16,
            fontWeight = "bold",
            background = "#FFFFFF",
            underline = true,
            strikethrough = false,
            onClick = null
        )

        assertEquals(0, attr.startIndex)
        assertEquals(10, attr.endIndex)
        assertEquals("#FF0000", attr.fontColor)
        assertEquals(16, attr.fontSize)
        assertEquals("bold", attr.fontWeight)
        assertEquals("#FFFFFF", attr.background)
        assertTrue(attr.underline)
        assertFalse(attr.strikethrough)
        assertNull(attr.onClick)
    }

    @Test
    fun `PartialAttribute with default values`() {
        val attr = PartialAttribute(
            startIndex = 5,
            endIndex = 15
        )

        assertEquals(5, attr.startIndex)
        assertEquals(15, attr.endIndex)
        assertNull(attr.fontColor)
        assertNull(attr.fontSize)
        assertNull(attr.fontWeight)
        assertNull(attr.background)
        assertFalse(attr.underline)
        assertFalse(attr.strikethrough)
        assertNull(attr.onClick)
    }

    @Test
    fun `fromJsonRange with List of Int creates attribute`() {
        val text = "Hello World Test"
        val range = listOf(0, 5)

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text,
            fontColor = "#FF0000"
        )

        assertNotNull(result)
        assertEquals(0, result!!.startIndex)
        assertEquals(5, result.endIndex)
        assertEquals("#FF0000", result.fontColor)
    }

    @Test
    fun `fromJsonRange with String pattern creates attribute`() {
        val text = "Hello World Test"
        val range = "World"

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text,
            fontWeight = "bold"
        )

        assertNotNull(result)
        assertEquals(6, result!!.startIndex) // "World" starts at index 6
        assertEquals(11, result.endIndex) // "World" ends at index 11
        assertEquals("bold", result.fontWeight)
    }

    @Test
    fun `fromJsonRange with String pattern not found returns null`() {
        val text = "Hello World Test"
        val range = "NotFound"

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text
        )

        assertNull(result)
    }

    @Test
    fun `fromJsonRange with invalid List size returns null`() {
        val text = "Hello World Test"
        val range = listOf(0) // Only one element

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text
        )

        assertNull(result)
    }

    @Test
    fun `fromJsonRange with empty List returns null`() {
        val text = "Hello World Test"
        val range = emptyList<Int>()

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text
        )

        assertNull(result)
    }

    @Test
    fun `fromJsonRange with invalid type returns null`() {
        val text = "Hello World Test"
        val range = 42 // Invalid type

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text
        )

        assertNull(result)
    }

    @Test
    fun `fromJsonRange with all optional parameters`() {
        val text = "Hello World Test"
        val range = listOf(0, 5)
        var clickCalled = false

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text,
            fontColor = "#FF0000",
            fontSize = 18,
            fontWeight = "semibold",
            background = "#EEEEEE",
            underline = true,
            strikethrough = true,
            onClick = { clickCalled = true }
        )

        assertNotNull(result)
        assertEquals("#FF0000", result!!.fontColor)
        assertEquals(18, result.fontSize)
        assertEquals("semibold", result.fontWeight)
        assertEquals("#EEEEEE", result.background)
        assertTrue(result.underline)
        assertTrue(result.strikethrough)
        assertNotNull(result.onClick)

        // Verify onClick callback works
        result.onClick?.invoke()
        assertTrue(clickCalled)
    }

    @Test
    fun `fromJsonRange with Double in List converts to Int`() {
        val text = "Hello World Test"
        val range = listOf(0.0, 5.0)

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text
        )

        assertNotNull(result)
        assertEquals(0, result!!.startIndex)
        assertEquals(5, result.endIndex)
    }

    @Test
    fun `PartialAttribute equals works correctly`() {
        val attr1 = PartialAttribute(
            startIndex = 0,
            endIndex = 10,
            fontColor = "#FF0000"
        )
        val attr2 = PartialAttribute(
            startIndex = 0,
            endIndex = 10,
            fontColor = "#FF0000"
        )
        val attr3 = PartialAttribute(
            startIndex = 0,
            endIndex = 10,
            fontColor = "#00FF00"
        )

        assertEquals(attr1, attr2)
        assertNotEquals(attr1, attr3)
    }

    @Test
    fun `PartialAttribute hashCode is consistent`() {
        val attr1 = PartialAttribute(
            startIndex = 0,
            endIndex = 10,
            fontColor = "#FF0000"
        )
        val attr2 = PartialAttribute(
            startIndex = 0,
            endIndex = 10,
            fontColor = "#FF0000"
        )

        assertEquals(attr1.hashCode(), attr2.hashCode())
    }

    @Test
    fun `PartialAttribute copy works correctly`() {
        val original = PartialAttribute(
            startIndex = 0,
            endIndex = 10,
            fontColor = "#FF0000"
        )
        val copied = original.copy(fontColor = "#00FF00")

        assertEquals("#FF0000", original.fontColor)
        assertEquals("#00FF00", copied.fontColor)
        assertEquals(original.startIndex, copied.startIndex)
        assertEquals(original.endIndex, copied.endIndex)
    }

    @Test
    fun `fromJsonRange with List of three elements returns null`() {
        val text = "Hello World Test"
        val range = listOf(0, 5, 10) // Three elements

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text
        )

        assertNull(result)
    }

    @Test
    fun `fromJsonRange finds first occurrence of pattern`() {
        val text = "Hello World World"
        val range = "World"

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text
        )

        assertNotNull(result)
        assertEquals(6, result!!.startIndex) // First "World"
        assertEquals(11, result.endIndex)
    }

    @Test
    fun `fromJsonRange with empty string pattern returns correct range`() {
        val text = "Hello World"
        val range = ""

        val result = PartialAttribute.fromJsonRange(
            range = range,
            text = text
        )

        // Empty string is found at index 0
        assertNotNull(result)
        assertEquals(0, result!!.startIndex)
        assertEquals(0, result.endIndex)
    }
}
