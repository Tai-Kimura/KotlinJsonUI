package com.kotlinjsonui.dynamic

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.JsonParser
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Where a vertical ScrollView puts a child narrower than itself.
 *
 * common.gravity is the CONTENT gravity of the view that declares it
 * ("Content gravity/alignment"; gravityDefaults top|start): the ScrollView's
 * own gravity places its content, and a child's gravity is the child's
 * content's, not the child's place. The Dynamic ScrollView read neither, so a
 * ScrollView declaring centerHorizontal drew its child at the start where web
 * centred it (2026-10-05). The kjui codegen arm is in jsonui-cli
 * scrollview_component_spec.rb; the conformance fixtures are
 * ScrollView/gravity__centerHorizontal and ScrollView/childGravity__right.
 */
@RunWith(AndroidJUnit4::class)
class ScrollViewContentGravityTest {

    @get:Rule
    val rule = createComposeRule()

    private fun layout(scrollGravity: String?, childGravity: String?): String {
        val scroll = scrollGravity?.let { ""","gravity":"$it"""" } ?: ""
        val child = childGravity?.let { ""","gravity":"$it"""" } ?: ""
        return """{"type":"View","id":"root","width":"matchParent","height":"matchParent","child":[
                   {"type":"ScrollView","id":"target","width":200,"height":200$scroll,"child":[
                     {"type":"View","id":"box","width":40,"height":40$child}]}]}"""
    }

    /** The box's x relative to the ScrollView, in dp, rounded. */
    private fun boxX(layout: String): Int {
        rule.setContent {
            DynamicView(json = JsonParser.parseString(layout).asJsonObject, data = emptyMap())
        }
        rule.waitForIdle()
        val d = rule.density.density
        val scroll = rule.onNodeWithTag("target").fetchSemanticsNode().positionInRoot
        val box = rule.onNodeWithTag("box").fetchSemanticsNode().positionInRoot
        return Math.round((box.x - scroll.x) / d)
    }

    @Test
    fun theScrollViewsOwnGravityCentresItsContent() {
        assertEquals(80, boxX(layout(scrollGravity = "centerHorizontal", childGravity = null)))
    }

    @Test
    fun theScrollViewsOwnGravityRightPlacesItsContentAtTheEnd() {
        assertEquals(160, boxX(layout(scrollGravity = "right", childGravity = null)))
    }

    @Test
    fun aChildsGravityDoesNotPlaceTheChild() {
        assertEquals(0, boxX(layout(scrollGravity = null, childGravity = "right")))
    }
}
