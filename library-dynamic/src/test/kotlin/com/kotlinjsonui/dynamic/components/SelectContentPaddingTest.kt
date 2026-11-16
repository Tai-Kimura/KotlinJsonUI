package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A select's content padding side by side (ticket kjui-selectbox-fixed-56dp-ignores-wrap-content-height, the three
 * faces' rule): SelectBoxDefaults 12 / 0 / 12 / 0, then paddings / padding, then each side's key; a side nothing
 * writes keeps the default. The same table as kjui's select_content_padding.
 */
class SelectContentPaddingTest {
    private fun sides(json: String): List<Float> {
        val p: PaddingValues = DynamicSelectBoxComponent.selectContentPadding(JsonParser.parseString(json).asJsonObject, emptyMap())
        val l = LayoutDirection.Ltr
        return listOf(p.calculateStartPadding(l), p.calculateTopPadding(), p.calculateEndPadding(l), p.calculateBottomPadding()).map { it.value }
    }

    @Test fun nothing_written_is_the_default() = assertEquals(listOf(12f, 0f, 12f, 0f), sides("{}"))
    @Test fun paddings_two_values_are_vertical_then_horizontal() = assertEquals(listOf(16f, 0f, 16f, 0f), sides("""{"paddings":[0,16]}"""))
    @Test fun a_side_key_alone_keeps_the_other_sides() = assertEquals(listOf(12f, 4f, 12f, 0f), sides("""{"paddingTop":4}"""))
    @Test fun a_side_key_wins_over_padding() = assertEquals(listOf(20f, 8f, 8f, 8f), sides("""{"padding":8,"paddingLeft":20}"""))
    @Test fun four_values_are_top_right_bottom_left() = assertEquals(listOf(4f, 1f, 2f, 3f), sides("""{"paddings":[1,2,3,4]}"""))
    @Test fun start_wins_over_left() = assertEquals(listOf(6f, 0f, 12f, 0f), sides("""{"paddingLeft":20,"paddingStart":6}"""))
}
