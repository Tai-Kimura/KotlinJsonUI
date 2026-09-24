package com.kotlinjsonui.dynamic.components

import androidx.compose.ui.Alignment
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * A View with no orientation is a Box, and its `gravity` becomes one
 * contentAlignment. A single value names ONE axis; the axis it does not name
 * takes the container default (jsonui-cli shared/core/attribute_semantics.json
 * -> gravityDefaults: top vertically, start horizontally). Until 2026-09-24 the
 * dynamic runtime centred that axis — `top` gave TopCenter, `left`
 * CenterStart — while the ios runtime and both codegens drew the same
 * declaration at the leading/top corner.
 */
class BoxContentAlignmentTest {

    private fun box(gravity: Any): Alignment {
        val json = JsonObject()
        when (gravity) {
            is String -> json.addProperty("gravity", gravity)
            is List<*> -> json.add("gravity", JsonArray().apply {
                gravity.forEach { add(JsonPrimitive(it as String)) }
            })
        }
        return DynamicContainerComponent.parseBoxContentAlignment(json)
    }

    // The defect: the axis a single value does not name.
    @Test fun top_alone_is_top_start() = assertEquals(Alignment.TopStart, box("top"))
    @Test fun bottom_alone_is_bottom_start() = assertEquals(Alignment.BottomStart, box("bottom"))
    @Test fun left_alone_is_top_start() = assertEquals(Alignment.TopStart, box("left"))
    @Test fun right_alone_is_top_end() = assertEquals(Alignment.TopEnd, box("right"))

    // Unchanged: these already took the default on the unnamed axis.
    @Test fun centerHorizontal_alone_is_top_center() = assertEquals(Alignment.TopCenter, box("centerHorizontal"))
    @Test fun centerVertical_alone_is_center_start() = assertEquals(Alignment.CenterStart, box("centerVertical"))
    @Test fun center_is_center() = assertEquals(Alignment.Center, box("center"))
    @Test fun no_gravity_is_top_start() = assertEquals(Alignment.TopStart, box(emptyList<String>()))

    // Both axes named: each axis as written.
    @Test fun top_right() = assertEquals(Alignment.TopEnd, box(listOf("top", "right")))
    @Test fun bottom_left() = assertEquals(Alignment.BottomStart, box("bottom|left"))
    @Test fun bottom_centerHorizontal() = assertEquals(Alignment.BottomCenter, box(listOf("bottom", "centerHorizontal")))
    @Test fun right_centerVertical() = assertEquals(Alignment.CenterEnd, box(listOf("right", "centerVertical")))
}
