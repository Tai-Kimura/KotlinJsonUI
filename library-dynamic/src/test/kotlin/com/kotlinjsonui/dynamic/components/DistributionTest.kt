package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Arrangement
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.generated.ViewAttributes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * `distribution`'s four values are TWO KINDS, not four arrangements.
 *
 * The vocabulary is UIStackView's (49-E ruling, measured across all three
 * toolchains):
 *
 *   fill / fillEqually        distribute SIZE among the children
 *   equalSpacing / equalCentering  distribute the FREE SPACE between them
 *
 * The size values are weights — `Modifier.weight` in Compose — not
 * `Arrangement.*`. Mapping `fill` to SpaceBetween is backwards: fill means
 * there is no free space left to distribute.
 *
 * What made this invisible: every platform collapsed a DIFFERENT pair of
 * values into one output, so two declared values that both differ from the
 * control but are identical to EACH OTHER pass `--inert-complete`, and android
 * parity passes too because codegen and dynamic collapse them the same way.
 * These assertions are the value-vs-value comparison the fixtures cannot make.
 */
class DistributionTest {

    private fun attrs(json: String): ViewAttributes =
        ViewAttributes.parse(TypedAttrs.toAttrMap(Gson().fromJson(json, JsonObject::class.java)))

    private fun distribution(value: String): String? =
        DynamicContainerComponent.distributionOf(attrs("""{"type":"View","distribution":"$value"}"""))

    @Test
    fun allFourValuesAreDeclaredAndDistinct() {
        val values = listOf("fill", "fillEqually", "equalSpacing", "equalCentering")
        val resolved = values.map { distribution(it) }
        assertEquals(values, resolved)
        assertEquals("the four values must stay four", 4, resolved.toSet().size)
    }

    @Test
    fun anUndeclaredDistributionIsNull() {
        assertNull(DynamicContainerComponent.distributionOf(attrs("""{"type":"View"}""")))
    }

    // ── the free-space kind IS an arrangement, and the two differ ──

    @Test
    fun equalSpacingIsSpaceBetween() {
        // Equal gaps between adjacent children, no leading or trailing gap.
        assertEquals(
            Arrangement.SpaceBetween,
            arrangementFor("equalSpacing")
        )
    }

    @Test
    fun equalCenteringIsSpaceAround() {
        // Each child centred in an equal track. NOT SpaceEvenly: equal gaps
        // everywhere leaves the outer children off-centre in their tracks.
        // A reached the same primitive on the web side (`justify-around`).
        assertEquals(Arrangement.SpaceAround, arrangementFor("equalCentering"))
    }

    @Test
    fun equalCenteringIsNotEqualSpacing() {
        // They used to collapse: android sent fillEqually AND equalCentering
        // both to SpaceEvenly, so no fixture could tell any pair apart.
        assertNotEquals(arrangementFor("equalSpacing"), arrangementFor("equalCentering"))
    }

    // ── the size kind is NOT an arrangement ──

    @Test
    fun theSizeValuesDoNotClaimAnArrangement() {
        // They are child weights. Leaving the arrangement at its default is
        // the point: `fill` means there is no free space to arrange.
        assertEquals(arrangementFor(null), arrangementFor("fill"))
        assertEquals(arrangementFor(null), arrangementFor("fillEqually"))
    }

    @Test
    fun theSizeValuesAreNotTheFreeSpaceValues() {
        assertNotEquals(arrangementFor("fill"), arrangementFor("equalSpacing"))
        assertNotEquals(arrangementFor("fillEqually"), arrangementFor("equalCentering"))
    }

    /** The REAL mapping — not a copy of it, or this test proves nothing. */
    private fun arrangementFor(distribution: String?): Arrangement.Horizontal {
        val json = Gson().fromJson(
            if (distribution == null) """{"type":"View"}"""
            else """{"type":"View","distribution":"$distribution"}""",
            JsonObject::class.java
        )
        return DynamicContainerComponent.parseHorizontalArrangement(
            ViewAttributes.parse(TypedAttrs.toAttrMap(json)), json, emptyMap()
        )
    }
}
