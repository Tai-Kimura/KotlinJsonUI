package com.kotlinjsonui.conformance

import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import com.google.gson.JsonElement
import com.google.gson.JsonObject as GsonObject
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
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
 * The testTag sits before the margins in KotlinJsonUI's modifier order
 * (testTag → margins → … ; ModifierBuilder.applyTestTag / applyMargins), and
 * a margin is drawn as padding. The tagged box is therefore the
 * margin-inclusive box — measured: the align fixtures' anchor reads (0, 0,
 * 170, 170). iOS and web report the box the view draws, so each frame is inset
 * by the margins that view draws as padding ([Margins.from], the same
 * ModifierBuilder.marginPaddings the renderer calls). A view whose margins are
 * bound resolves them against the fixture's runtime data, which this reader
 * does not see; it is left out, and the gate names it as absent.
 */
object ConformanceFrames {
    const val SOURCE = "compose-layout-coordinates"

    data class Box(val x: Float, val y: Float, val width: Float, val height: Float)

    data class Read(
        val tags: Map<String, Box>,
        val duplicates: Set<String>,
        val density: Float,
    )

    /** Per-id margins the layout draws as padding, and the ids whose margins are bound. */
    data class Margins(val byId: Map<String, ModifierBuilder.MarginPaddings>, val bound: Set<String>) {
        companion object {
            val NONE = Margins(emptyMap(), emptySet())

            fun from(layout: GsonObject): Margins {
                val byId = mutableMapOf<String, ModifierBuilder.MarginPaddings>()
                val bound = mutableSetOf<String>()
                fun visit(e: JsonElement) {
                    when {
                        e.isJsonObject -> {
                            val o = e.asJsonObject
                            val id = o.get("id")?.takeIf { it.isJsonPrimitive }?.asString
                            if (id != null) {
                                val marginKeys = o.keySet().filter { it == "margins" || it.endsWith("Margin") }
                                val isBound = marginKeys.any { k ->
                                    val v = o.get(k)
                                    v.isJsonPrimitive && v.asJsonPrimitive.isString && v.asString.contains("@{")
                                }
                                if (isBound) bound += id
                                else ModifierBuilder.marginPaddings(o, emptyMap())?.let { byId[id] = it }
                            }
                            o.entrySet().forEach { (k, v) -> if (k != "_generated") visit(v) }
                        }
                        e.isJsonArray -> e.asJsonArray.forEach { visit(it) }
                    }
                }
                visit(layout)
                return Margins(byId, bound)
            }
        }
    }

    /** Must run on the main thread. Every Compose root under [decorView]. */
    fun read(decorView: View, margins: Margins = Margins.NONE): Read {
        val density = decorView.resources.displayMetrics.density
        val found = LinkedHashMap<String, MutableList<Box>>()
        composeRoots(decorView).forEach { root ->
            walk(root.semanticsOwner.unmergedRootSemanticsNode) { node ->
                val tag = node.config.getOrNull(SemanticsProperties.TestTag) ?: return@walk
                if (!node.layoutInfo.isPlaced || tag in margins.bound) return@walk
                val p = node.positionInWindow
                val m = margins.byId[tag]
                // Start / end are left / right: the conformance emulator is LTR.
                val l = m?.start ?: 0f
                val t = m?.top ?: 0f
                found.getOrPut(tag) { mutableListOf() }.add(
                    Box(p.x / density + l, p.y / density + t,
                        node.size.width / density - l - (m?.end ?: 0f),
                        node.size.height / density - t - (m?.bottom ?: 0f))
                )
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
