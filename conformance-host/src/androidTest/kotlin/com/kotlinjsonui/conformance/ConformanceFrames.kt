package com.kotlinjsonui.conformance

import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import kotlin.math.roundToLong
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Where each testTag was drawn, for the conformance gate's frame parity
 * (jsonui-cli conformance/frames.schema.json, RESULTS_SCHEMA.md `frames`).
 *
 * Read in-process from Compose semantics, at the moment the screenshot was
 * taken: every unmerged semantics node that carries a testTag (KotlinJsonUI
 * sets the layout `id` as the testTag), its position in the window and its
 * size, in px, divided by the display density to dp. Position and size come
 * from the node's own layout coordinates, not from its bounds: `boundsInRoot`
 * and the accessibility bounds are clipped to the parents and the screen, and
 * a view misplaced off the top of the screen (KotlinJsonUI 2.43.3's
 * alignTopOfView target, at y = -50) would then read as a clipped sliver
 * instead of where it is. `positionInWindow` goes through the layer
 * transforms, so a graphicsLayer translation is included
 * (ConformanceFramesTest holds that).
 *
 * The box is the testTag MODIFIER's own layout coordinates (the ModifierInfo
 * of its TestTagElement), not the bounds of the semantics node it merges
 * into. Material3's Slider and LinearProgressIndicator widen their semantics
 * bounds by 10 dp per side for TalkBack (AccessibilityUtil.kt,
 * IncreaseHorizontal / VerticalSemanticsBounds) without changing their
 * layout: the merged node read x -10 / width 220 for a 200-wide Slider and
 * y -10 / height 24 for a Progress (found by support lane 1). The tag's own
 * coordinates are the box at that point of the chain — the drawn box.
 *
 * KotlinJsonUI places the testTag inside the margins and the offset (margins
 * → size → offset → testTag → alpha → …, since 2.43.5), so the tagged box is
 * the drawn box and is read as it is. Before that the tag came first and a
 * margined view's tagged box included its margin (ticket
 * kjui-a11y-bounds-of-a-margined-view-include-its-margin); this reader inset
 * it from the layout, and that step is gone with the fix.
 */
object ConformanceFrames {
    const val SOURCE = "compose-layout-coordinates"

    data class Box(val x: Float, val y: Float, val width: Float, val height: Float)

    data class Read(
        val tags: Map<String, Box>,
        val duplicates: Set<String>,
        val density: Float,
    )

    /** Must run on the main thread. Every Compose root under [decorView]. */
    fun read(decorView: View): Read {
        val density = decorView.resources.displayMetrics.density
        val found = LinkedHashMap<String, MutableList<Box>>()
        composeRoots(decorView).forEach { root ->
            walk(root.semanticsOwner.unmergedRootSemanticsNode) { node ->
                val tag = node.config.getOrNull(SemanticsProperties.TestTag) ?: return@walk
                if (!node.layoutInfo.isPlaced) return@walk
                val c = tagCoordinates(node, tag)
                val box = if (c != null) {
                    val p = c.positionInWindow()
                    Box(p.x / density, p.y / density, c.size.width / density, c.size.height / density)
                } else {
                    // No TestTagElement on this layout node (a tag set through
                    // a plain semantics block): the node's own position.
                    val p = node.positionInWindow
                    Box(p.x / density, p.y / density, node.size.width / density, node.size.height / density)
                }
                found.getOrPut(tag) { mutableListOf() }.add(box)
            }
        }
        val duplicates = found.filterValues { it.size > 1 }.keys
        val tags = found.filterKeys { it !in duplicates }.mapValues { it.value.single() }
        return Read(tags, duplicates, density)
    }

    /**
     * The frames document, or null when there is no single `root` — the gate
     * then counts "no frames file" rather than reading a guess. Frames are
     * relative to the element with id `root` (schema: Android's root box).
     */
    fun toJson(fixtureId: String, read: Read): JsonObject? {
        val root = read.tags["root"] ?: return null
        return buildJsonObject {
            put("schemaVersion", 1)
            put("fixture", fixtureId)
            put("platform", "android")
            put("source", SOURCE)
            put("density", round(read.density))
            put("root", box(root, 0f, 0f))
            put("frames", buildJsonObject {
                read.tags.keys.sorted().forEach { tag -> put(tag, box(read.tags.getValue(tag), root.x, root.y)) }
            })
            if (read.duplicates.isNotEmpty()) {
                put("duplicates", buildJsonArray { read.duplicates.sorted().forEach { add(kotlinx.serialization.json.JsonPrimitive(it)) } })
            }
        }
    }

    private fun box(b: Box, originX: Float, originY: Float) = buildJsonObject {
        put("x", round(b.x - originX))
        put("y", round(b.y - originY))
        put("width", round(b.width))
        put("height", round(b.height))
    }

    private fun round(v: Float): Double = (v * 100.0).roundToLong() / 100.0

    /**
     * The layout coordinates the testTag modifier with this [tag] sits on: the
     * ModifierInfo of the node's TestTagElement. The element class is internal
     * to Compose, so it is found by name and its tag read reflectively; with
     * one TestTagElement on the node the tag is not needed to choose.
     */
    private fun tagCoordinates(node: SemanticsNode, tag: String): LayoutCoordinates? {
        val infos = node.layoutInfo.getModifierInfo()
            .filter { it.modifier.javaClass.simpleName == "TestTagElement" }
        if (infos.isEmpty()) return null
        val named = infos.firstOrNull { info ->
            runCatching {
                info.modifier.javaClass.getDeclaredField("tag").apply { isAccessible = true }.get(info.modifier)
            }.getOrNull() == tag
        }
        return (named ?: infos.singleOrNull())?.coordinates
    }

    private fun walk(node: SemanticsNode, visit: (SemanticsNode) -> Unit) {
        visit(node)
        node.children.forEach { walk(it, visit) }
    }

    private fun composeRoots(view: View): List<ViewRootForTest> {
        val out = mutableListOf<ViewRootForTest>()
        fun visit(v: View) {
            if (v is ViewRootForTest) out += v
            if (v is ViewGroup) for (i in 0 until v.childCount) visit(v.getChildAt(i))
        }
        visit(view)
        return out
    }
}
