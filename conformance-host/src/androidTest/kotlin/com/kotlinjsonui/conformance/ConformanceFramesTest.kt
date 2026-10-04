package com.kotlinjsonui.conformance

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DynamicView
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * What ConformanceFrames records, against specimens whose place is known from
 * their declaration. The frames feed the gate's cross-platform comparison, so
 * each way a view can be moved must land in the number: a layout offset, a
 * graphicsLayer translation (a draw-time move — if the coordinates missed it,
 * the frame would disagree with the picture), and a view that sits outside a
 * clipping parent (a bounds-based reading would cut it to the visible part).
 */
@RunWith(AndroidJUnit4::class)
class ConformanceFramesTest {

    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private fun frames(): ConformanceFrames.Read {
        rule.waitForIdle()
        var read: ConformanceFrames.Read? = null
        rule.runOnUiThread { read = ConformanceFrames.read(rule.activity.window.decorView) }
        return read!!
    }

    private fun near(expected: Float, actual: Float, what: String) = assertEquals(what, expected, actual, 0.5f)

    private fun specimens() {
        rule.setContent {
            val px60 = with(LocalDensity.current) { 60.dp.toPx() }
            Box(Modifier.fillMaxSize().testTag("root")) {
                Box(Modifier.offset(x = 120.dp, y = 30.dp).size(50.dp).testTag("offset"))
                Box(Modifier.padding(top = 200.dp).graphicsLayer { translationX = px60 }.size(50.dp).testTag("layer"))
                Box(Modifier.padding(top = 300.dp).size(100.dp).clipToBounds()) {
                    Box(Modifier.offset(x = (-30).dp, y = (-40).dp).size(50.dp).testTag("outside"))
                }
                Box(Modifier.padding(top = 500.dp).size(10.dp).testTag("twice"))
                Box(Modifier.padding(top = 520.dp).size(10.dp).testTag("twice"))
            }
        }
    }

    private fun rel(read: ConformanceFrames.Read, tag: String): ConformanceFrames.Box {
        val root = read.tags.getValue("root")
        val b = read.tags.getValue(tag)
        return ConformanceFrames.Box(b.x - root.x, b.y - root.y, b.width, b.height)
    }

    @Test
    fun aLayoutOffsetIsInThePosition() {
        specimens()
        val b = rel(frames(), "offset")
        near(120f, b.x, "offset x")
        near(30f, b.y, "offset y")
        near(50f, b.width, "offset width")
    }

    @Test
    fun aGraphicsLayerTranslationIsInThePosition() {
        specimens()
        val b = rel(frames(), "layer")
        near(60f, b.x, "graphicsLayer translationX 60dp moves the frame as it moves the picture")
        near(200f, b.y, "layer y")
    }

    @Test
    fun aViewOutsideAClippingParentKeepsItsWholeFrame() {
        specimens()
        val b = rel(frames(), "outside")
        near(-30f, b.x, "x outside the clipping parent, not cut to 0")
        near(260f, b.y, "y = 300 - 40, not cut to the parent's top")
        near(50f, b.width, "the whole width, not the visible 20")
        near(50f, b.height, "the whole height, not the visible 10")
    }

    @Test
    fun aRepeatedTagIsNamedNotGuessed() {
        specimens()
        val read = frames()
        assertTrue(read.duplicates.contains("twice"))
        assertTrue(!read.tags.containsKey("twice"))
    }

    @Test
    fun theDocumentIsRelativeToRootAndNamesItsSource() {
        specimens()
        val doc = ConformanceFrames.toJson("a/b__c", frames())!!
        assertEquals("compose-layout-coordinates", doc["source"]!!.jsonPrimitive.content)
        assertEquals("android", doc["platform"]!!.jsonPrimitive.content)
        assertTrue(doc["density"]!!.jsonPrimitive.content.toDouble() > 0)
        val frames = doc["frames"]!!.jsonObject
        assertEquals(0.0, frames["root"]!!.jsonObject["x"]!!.jsonPrimitive.content.toDouble(), 0.0)
        assertEquals(120.0, frames["offset"]!!.jsonObject["x"]!!.jsonPrimitive.content.toDouble(), 0.5)
        assertEquals(listOf("twice"), doc["duplicates"]!!.jsonArray.map { it.jsonPrimitive.content })
    }

    /**
     * KotlinJsonUI draws a margin as padding around the view's own box. Since
     * 2.43.5 the testTag sits inside it, so the tagged box, read as it is, IS
     * the drawn box: 120..170 for the align fixtures' anchor. Before, the tag
     * sat outside and this read 0..170 (ticket
     * kjui-a11y-bounds-of-a-margined-view-include-its-margin). This arm fails
     * if the tag moves back out.
     */
    @Test
    fun aMarginedViewReportsTheBoxItDraws() {
        val json = JsonParser.parseString(
            """{"type": "View", "id": "root", "width": "matchParent", "height": "matchParent", "child": [
                 {"type": "View", "id": "anchor", "width": 50, "height": 50, "background": "#CCCCCC",
                  "topMargin": 120, "leftMargin": 120},
                 {"type": "View", "id": "target", "width": 200, "height": 200, "alignTopView": "anchor"}
               ]}"""
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = emptyMap()) }
        val read = frames()
        val anchor = rel(read, "anchor")
        near(120f, anchor.x, "anchor x = its drawn left, not its ref box's 0")
        near(120f, anchor.y, "anchor y = its drawn top")
        near(50f, anchor.width, "anchor width = 50, not 170")
        near(50f, anchor.height, "anchor height = 50, not 170")
        val target = rel(read, "target")
        near(120f, target.y, "target aligned to the anchor's drawn top")
    }

    /** The offset stage is outside the testTag too: the tagged box moves with the drawing. */
    @Test
    fun aDeclaredOffsetMovesTheTaggedBox() {
        val json = JsonParser.parseString(
            """{"type": "View", "id": "root", "width": "matchParent", "height": "matchParent", "child": [
                 {"type": "View", "id": "target", "width": 200, "height": 200, "offsetX": 8, "offsetY": 12,
                  "topMargin": 20}
               ]}"""
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = emptyMap()) }
        val target = rel(frames(), "target")
        near(8f, target.x, "offsetX 8")
        near(32f, target.y, "topMargin 20 + offsetY 12")
        near(200f, target.width, "the drawn width")
        near(200f, target.height, "the drawn height")
    }

    /**
     * The control for the two arms above: the reader reports the box the tag
     * sits on, and corrects nothing. A tag placed OUTSIDE a padding (the shape
     * KotlinJsonUI had before 2.43.5) reads the padding-inclusive box; one
     * placed inside reads the drawn box. So those arms measure the library's
     * order, not a correction made here.
     */
    @Test
    fun theReaderReportsTheBoxTheTagSitsOn() {
        rule.setContent {
            Box(Modifier.fillMaxSize().testTag("root")) {
                Box(Modifier.testTag("outer").padding(start = 120.dp, top = 120.dp).size(50.dp))
                Box(Modifier.padding(start = 120.dp, top = 300.dp).testTag("inner").size(50.dp))
            }
        }
        val read = frames()
        val outer = rel(read, "outer")
        near(0f, outer.x, "outer tag x")
        near(170f, outer.width, "outer tag box includes the padding")
        val inner = rel(read, "inner")
        near(120f, inner.x, "inner tag x")
        near(50f, inner.width, "inner tag box is the drawn box")
    }

    /** A bound margin resolves at runtime, and the frame reads where it put the view. */
    @Test
    fun aViewWithBoundMarginsIsMeasured() {
        val json = JsonParser.parseString(
            """{"type": "View", "id": "root", "width": "matchParent", "height": "matchParent", "child": [
                 {"type": "View", "id": "anchor", "width": 50, "height": 50, "topMargin": "@{m}"}
               ]}"""
        ).asJsonObject
        rule.setContent { DynamicView(json = json, data = mapOf("m" to 20)) }
        val anchor = rel(frames(), "anchor")
        near(20f, anchor.y, "the bound topMargin")
        near(50f, anchor.height, "the drawn height")
    }

    @Test
    fun noSingleRootWritesNoDocument() {
        rule.setContent { Box(Modifier.size(10.dp).testTag("target")) }
        assertNull(ConformanceFrames.toJson("a/b__c", frames()))
    }
}
