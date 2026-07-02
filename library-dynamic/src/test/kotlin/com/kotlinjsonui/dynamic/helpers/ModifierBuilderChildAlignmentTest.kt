package com.kotlinjsonui.dynamic.helpers

import androidx.compose.ui.Alignment
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Child OUTER placement must come from the individual align/center booleans
 * only. `gravity` positions a node's own children (inner content) and must
 * never contribute to where the node sits inside its parent — otherwise a
 * child like `{ gravity: "center", alignBottom: true, alignRight: true }`
 * (e.g. a FAB whose label is centered) gets pulled to the parent's center
 * instead of bottom-end. Reference semantics: build_alignment /
 * build_relative_positioning in the static modifier_builder.rb, which never
 * read `gravity`.
 */
class ModifierBuilderChildAlignmentTest {

    private fun node(build: JsonObject.() -> Unit): JsonObject =
        JsonObject().apply(build)

    // ── Box parent ──

    @Test
    fun `box child - alignBottom+alignRight win over gravity center`() {
        val json = node {
            addProperty("gravity", "center")
            addProperty("alignBottom", true)
            addProperty("alignRight", true)
        }
        assertEquals(Alignment.BottomEnd, ModifierBuilder.getChildAlignment(json, "Box"))
    }

    @Test
    fun `box child - gravity alone does not place the child`() {
        assertNull(ModifierBuilder.getChildAlignment(node { addProperty("gravity", "center") }, "Box"))
        assertNull(ModifierBuilder.getChildAlignment(node { addProperty("gravity", "bottom|right") }, "Box"))
    }

    @Test
    fun `box child - individual booleans still honored`() {
        assertEquals(
            Alignment.BottomEnd,
            ModifierBuilder.getChildAlignment(
                node {
                    addProperty("alignBottom", true)
                    addProperty("alignRight", true)
                },
                "Box"
            )
        )
        assertEquals(
            Alignment.Center,
            ModifierBuilder.getChildAlignment(node { addProperty("centerInParent", true) }, "Box")
        )
    }

    // ── Row parent ──

    @Test
    fun `row child - gravity alone does not place the child`() {
        assertNull(ModifierBuilder.getChildAlignment(node { addProperty("gravity", "center") }, "Row"))
        assertNull(ModifierBuilder.getChildAlignment(node { addProperty("gravity", "bottom") }, "Row"))
    }

    @Test
    fun `row child - alignBottom boolean wins over gravity center`() {
        val json = node {
            addProperty("gravity", "center")
            addProperty("alignBottom", true)
        }
        assertEquals(Alignment.Bottom, ModifierBuilder.getChildAlignment(json, "Row"))
    }

    // ── Column parent ──

    @Test
    fun `column child - gravity alone does not place the child`() {
        assertNull(ModifierBuilder.getChildAlignment(node { addProperty("gravity", "center") }, "Column"))
        assertNull(ModifierBuilder.getChildAlignment(node { addProperty("gravity", "right") }, "Column"))
    }

    @Test
    fun `column child - alignRight boolean wins over gravity center`() {
        val json = node {
            addProperty("gravity", "center")
            addProperty("alignRight", true)
        }
        assertEquals(Alignment.End, ModifierBuilder.getChildAlignment(json, "Column"))
    }

    // ── ConstraintLayout parent constraints ──

    @Test
    fun `relative positioning - gravity does not link the node to its parent`() {
        val constraints = ModifierBuilder.buildRelativePositioning(
            node { addProperty("gravity", "center") },
            emptyMap()
        )
        assertTrue("gravity must not emit parent constraints: $constraints", constraints.isEmpty())
    }

    @Test
    fun `relative positioning - align booleans keep only their own parent links`() {
        val constraints = ModifierBuilder.buildRelativePositioning(
            node {
                addProperty("gravity", "center")
                addProperty("alignBottom", true)
                addProperty("alignRight", true)
            },
            emptyMap()
        )
        assertEquals(
            listOf("bottom.linkTo(parent.bottom)", "end.linkTo(parent.end)"),
            constraints
        )
    }

    // ── outerAlignFlags itself ──

    @Test
    fun `outerAlignFlags ignores gravity but reads all booleans`() {
        val json = node {
            addProperty("gravity", "center")
            addProperty("alignTop", true)
            addProperty("centerHorizontal", true)
        }
        val flags = ModifierBuilder.outerAlignFlags(json)
        assertEquals(
            ModifierBuilder.AlignFlags(alignTop = true, centerH = true),
            flags
        )
    }
}
