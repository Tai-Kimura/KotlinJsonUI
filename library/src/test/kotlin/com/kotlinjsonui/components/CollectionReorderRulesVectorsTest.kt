package com.kotlinjsonui.components

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.components.CollectionReorderRules.RowSpan
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The rule against jsonui-cli's shared/core/reorder_vectors.json (copied to src/test/resources, held to its source at
 * JSONUI_CLI_MANIFEST_REF by CI): every case of the drag, the drop, the release, the auto-scroll and the
 * accessibility moves gives the table's answer. iosOnMove is SwiftUI's conversion and has no Android twin.
 */
class CollectionReorderRulesVectorsTest {
    private val vectors: JsonObject = JsonParser.parseReader(
        requireNotNull(javaClass.classLoader?.getResourceAsStream("reorder_vectors.json")).reader(),
    ).asJsonObject

    private fun cases(name: String): List<JsonObject> = vectors.getAsJsonArray(name).map { it.asJsonObject }
    private fun ints(a: JsonArray?): List<Int> = a?.map { it.asInt } ?: emptyList()
    private fun spans(heights: List<Int>, top: Float = 0f): List<RowSpan> {
        var y = top
        return heights.map { h -> RowSpan(y, y + h).also { y += h } }
    }

    @Test fun every_drag_case() {
        val all = cases("drag")
        assertTrue(all.isNotEmpty())
        for (c in all) {
            val locked = ints(c.getAsJsonArray("locked")).toSet()
            val fixed = ints(c.getAsJsonArray("fixed")).toSet()
            if (c["to"].isJsonNull) {                     // not dragged at all
                assertTrue(c["name"].asString, !CollectionReorderRules.canPickUp(c["from"].asInt, locked, fixed))
                continue
            }
            val to = CollectionReorderRules.target(c["from"].asInt, c["dy"].asFloat, spans(ints(c.getAsJsonArray("heights"))), locked)
            assertEquals(c["name"].asString, c["to"].asInt, to)
        }
    }

    @Test fun every_drop_case_reads_to_as_the_index_after_the_move() {
        for (c in cases("drop")) {
            val rows = c.getAsJsonArray("rows").map { it.asString }.toMutableList()
            val refused = CollectionReorderRules.refusal(
                rows.size, c["from"].asInt, c["to"].asInt, ints(c.getAsJsonArray("locked")).toSet(),
                ints(c.getAsJsonArray("fixed")).toSet(),
            )
            assertEquals(c["name"].asString, c["refused"]?.asString, refused)
            if (refused != null) continue
            val row = rows.removeAt(c["from"].asInt)
            rows.add(c["to"].asInt, row)
            assertEquals(c["name"].asString, c.getAsJsonArray("order").map { it.asString }, rows)
        }
    }

    @Test fun every_release_case() {
        val all = cases("release")
        assertTrue(all.isNotEmpty())
        for (c in all) {
            val header = c["header"].asFloat
            val heights = ints(c.getAsJsonArray("heights"))
            val rows = spans(heights, header)
            val from = c["from"].asInt
            val dy = c["dy"].asFloat
            val finger = rows[from].top + c["grabAt"].asFloat + dy
            val bottom = header + heights.sum() + c["footer"].asFloat
            val commits = CollectionReorderRules.releaseCommits(finger, 0f, bottom)
            val cancelled = c.has("cancelled") && c["cancelled"].asBoolean
            assertEquals(c["name"].asString, !cancelled, commits)
            val fixed = ints(c.getAsJsonArray("fixed")).toSet()
            if (!cancelled && c["to"].isJsonNull) {       // a row that is not picked up
                assertTrue(c["name"].asString, !CollectionReorderRules.canPickUp(from, ints(c.getAsJsonArray("locked")).toSet(), fixed))
                continue
            }
            if (!cancelled) {
                val to = CollectionReorderRules.target(from, dy, rows, ints(c.getAsJsonArray("locked")).toSet())
                assertEquals(c["name"].asString, c["to"].asInt, to)
            }
        }
    }

    @Test fun every_auto_scroll_case() {
        val all = cases("autoScroll")
        assertTrue(all.isNotEmpty())
        for (c in all) {
            val v = CollectionReorderRules.autoScrollVelocity(c["fingerY"].asFloat, 0f, c["viewport"].asFloat)
            assertEquals(c["name"].asString, c["speed"].asFloat, v, 0.001f)
        }
    }

    @Test fun every_accessibility_case() {
        val all = cases("a11y")
        assertTrue(all.isNotEmpty())
        for (c in all) {
            val from = c["from"].asInt
            val (up, down) = CollectionReorderRules.accessibilityMoves(
                from, c["rows"].asInt, ints(c.getAsJsonArray("locked")).toSet(), ints(c.getAsJsonArray("fixed")).toSet(),
            )
            val offered = if (c["action"].asString == "up") up else down
            val want = c.has("to") && !c["to"].isJsonNull
            assertEquals(c["name"].asString, want, offered)
            if (want) assertEquals(c["name"].asString, c["to"].asInt, if (c["action"].asString == "up") from - 1 else from + 1)
        }
    }
}
