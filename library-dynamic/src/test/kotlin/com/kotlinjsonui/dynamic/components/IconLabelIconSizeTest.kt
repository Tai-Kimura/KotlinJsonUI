package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * attribute_semantics iconLabelIconSize (2026-10-05 ruling): when `iconSize`
 * is not declared, the icon is drawn at the image's own size, so
 * [DynamicIconLabelComponent.iconSizeFor] gives no size and the Image gets no
 * size modifier. It used to give 24 x 24, which drew a 64 dp asset at 24.
 */
class IconLabelIconSizeTest {

    @Test
    fun undeclaredGivesNoSize() {
        assertNull(DynamicIconLabelComponent.iconSizeFor(null))
    }

    @Test
    fun aNumberSizesBothEdges() {
        assertEquals(40f to 40f, DynamicIconLabelComponent.iconSizeFor(40))
    }

    @Test
    fun aPairSizesTheEdgesSeparately() {
        assertEquals(40f to 20f, DynamicIconLabelComponent.iconSizeFor(listOf(40, 20)))
    }

    @Test
    fun aMissingHeightFollowsTheWidth() {
        assertEquals(40f to 40f, DynamicIconLabelComponent.iconSizeFor(listOf(40)))
    }

    @Test
    fun anArrayWithoutAWidthGivesNoSize() {
        assertNull(DynamicIconLabelComponent.iconSizeFor(emptyList<Int>()))
    }
}
