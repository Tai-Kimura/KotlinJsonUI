package com.kotlinjsonui.conformance

import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.helpers.ModifierBuilder
import com.kotlinjsonui.dynamic.helpers.ResourceResolver
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A custom component scaffolded as a leaf (`kjui g converter <Name>
 * --no-container`) that a layout gives children, on device — NOT part of the
 * conformance suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.LeafChildrenProbeTest \
 *     -e leafChildrenProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * The wrappers below are what kjui generates (jsonui-cli 1.8.121 and, for
 * `OldLeaf`, 1.8.121's base e1a85ca2), bodies as written; the composables are
 * the scaffolds with a body the test can see. The old leaf wrapper never read
 * its children: they were not drawn and nothing said so. The new one checks
 * first and draws the refusal in the component's place, and logs it, as
 * `jui build` refuses the layout in codegen — the check written into the
 * wrapper, not called from this library, so a wrapper generated for a face
 * on an older KotlinJsonUI still compiles. The sentence is jsonui-cli
 * shared/core/leaf_children_vectors.json's (SwiftJsonUI shows the same).
 * `lp_control` is the instrument's positive control.
 *
 * Measured on an API 35 emulator, 2026-09-25: the old wrapper — "old leaf
 * drawn", its child 0, no refusal; the new one — the refusal in its place,
 * the leaf not drawn, the child 0; a leaf with no children drawn; the
 * default mode's child found once.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class LeafChildrenProbeTest {

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("leafChildrenProbe") == "1"
        Assume.assumeTrue("set -e leafChildrenProbe 1", enabled)
    }

    @After
    fun unregister() {
        Configuration.customComponentHandler = null
    }

    @Test
    fun aLeafGivenChildrenIsRefusedInItsPlace() {
        Configuration.customComponentHandler = { type, json, data ->
            when (type) {
                "LeafProbeLeaf" -> { DynamicLeafProbeLeafComponent.create(json, data); true }
                "LeafProbeOldLeaf" -> { DynamicLeafProbeOldLeafComponent.create(json, data); true }
                "LeafProbeAuto" -> { DynamicLeafProbeAutoComponent.create(json, data); true }
                else -> false
            }
        }
        val layout = """{"type": "View", "orientation": "vertical", "child": [
          {"type": "LeafProbeLeaf", "id": "lp_leaf", "title": "leaf drawn", "child": [{"type": "Label", "id": "lp_leaf_kid", "text": "Leaf kid"}]},
          {"type": "LeafProbeOldLeaf", "id": "lp_old_leaf", "title": "old leaf drawn", "child": [{"type": "Label", "id": "lp_old_kid", "text": "Old kid"}]},
          {"type": "LeafProbeLeaf", "id": "lp_leaf_alone", "title": "alone drawn"},
          {"type": "LeafProbeAuto", "id": "lp_auto", "title": "auto", "child": [{"type": "Label", "id": "lp_auto_kid", "text": "Auto kid"}]},
          {"type": "Label", "id": "lp_control", "text": "control"}
        ]}"""
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                Column(Modifier.padding(top = 80.dp).semantics { testTagsAsResourceId = true }) {
                    DynamicView(json = JsonParser.parseString(layout).asJsonObject, data = emptyMap())
                }
            }
        }
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.wait(Until.hasObject(By.res("lp_control")), 10_000)
        fun byRes(id: String) = device.findObjects(By.res(id)).size
        val refusal = device.findObject(By.textContains("'LeafProbeLeaf' (id=lp_leaf) takes no children"))
        listOf("lp_control", "lp_leaf_kid", "lp_old_kid", "lp_auto_kid").forEach { println("LCP id $it: count=${byRes(it)}") }
        listOf("leaf drawn", "old leaf drawn", "alone drawn").forEach { println("LCP text '$it': ${device.findObject(By.text(it)) != null}") }
        println("LCP refusal: ${refusal?.text}")
        println("LCP old refusal: ${device.findObject(By.textContains("'LeafProbeOldLeaf'"))?.text}")

        assertEquals("the instrument answers 1 where there is one", 1, byRes("lp_control"))
        // The new leaf: its child is not drawn, and it says so in its place.
        assertEquals(0, byRes("lp_leaf_kid"))
        assertNotNull("the refusal is drawn", refusal)
        assertEquals(true, refusal!!.text.contains("child[0] (id=lp_leaf_kid) is not drawn"))
        assertNull("the leaf was drawn without its children", device.findObject(By.text("leaf drawn")))
        // …and says so in the log, by name.
        val log = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("logcat -d -s DynamicView:W")
            .let { fd -> android.os.ParcelFileDescriptor.AutoCloseInputStream(fd).bufferedReader().readText() }
        println("LCP log: ${log.lines().lastOrNull { it.contains("takes no children") }}")
        assertEquals(true, log.contains("'LeafProbeLeaf' (id=lp_leaf) takes no children"))
        // The old wrapper, for the record: drawn, its child not, nothing said.
        assertEquals(0, byRes("lp_old_kid"))
        assertNotNull(device.findObject(By.text("old leaf drawn")))
        assertNull(device.findObject(By.textContains("'LeafProbeOldLeaf'")))
        // A leaf with no children is drawn; the default mode draws its children.
        assertNotNull(device.findObject(By.text("alone drawn")))
        assertEquals(1, byRes("lp_auto_kid"))
        scenario.close()
    }
}

// ---- the scaffolds (composables), with a body the test can see ----

@Composable
fun LeafProbeLeaf(title: String = "", modifier: Modifier = Modifier) {
    Text(text = title, modifier = modifier)
}

@Composable
fun LeafProbeOldLeaf(title: String = "", modifier: Modifier = Modifier) {
    Text(text = title, modifier = modifier)
}

@Composable
fun LeafProbeAuto(title: String = "", modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit = {}) {
    Box(modifier = modifier) { content() }
}

// ---- the Dynamic wrappers, as kjui generates them ----

object DynamicLeafProbeLeafComponent {
    @Composable
    fun create(
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ) {
        val context = LocalContext.current

        // Parse attributes from JSON with binding support
        val title = ResourceResolver.resolveText(json, "title", data, context)

        // Build modifier
        val modifier = ModifierBuilder.buildModifier(json, data, context = context)

        leafRejection(json)?.let { message ->
            android.util.Log.w("DynamicView", message)
            androidx.compose.material3.Text(text = "⚠️ $message", color = androidx.compose.ui.graphics.Color.Red)
            return
        }
        LeafProbeLeaf(
            title = title,
            modifier = modifier
        )
    }
    
    /** What a layout that gives this leaf children is told (null: it gives none). */
    private fun leafRejection(json: JsonObject): String? {
        val key = if (json.has("child")) "child" else "children"
        val value = json.get(key) ?: return null
        fun isNode(e: com.google.gson.JsonElement) = e.isJsonObject && (e.asJsonObject.has("type") || e.asJsonObject.has("include"))
        fun idOf(o: JsonObject) = o.get("id")?.takeIf { it.isJsonPrimitive }?.let { " (id=" + it.asString + ")" } ?: ""
        val dropped = when {
            value.isJsonArray -> value.asJsonArray.mapIndexedNotNull { i, e -> if (isNode(e)) "$key[$i]" + idOf(e.asJsonObject) else null }
            isNode(value) -> listOf(key + idOf(value.asJsonObject))
            else -> emptyList()
        }
        if (dropped.isEmpty()) return null
        return "'LeafProbeLeaf'" + idOf(json) + " takes no children — it is declared a leaf, so " +
            dropped.joinToString(", ") + (if (dropped.size == 1) " is" else " are") +
            " not drawn. Remove the children, or regenerate the component with --container."
    }

}

/** The leaf wrapper jsonui-cli e1a85ca2 generates: children are never read. */
object DynamicLeafProbeOldLeafComponent {
    @Composable
    fun create(
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ) {
        val context = LocalContext.current

        // Parse attributes from JSON with binding support
        val title = ResourceResolver.resolveText(json, "title", data, context)

        // Build modifier
        val modifier = ModifierBuilder.buildModifier(json, data, context = context)

        LeafProbeOldLeaf(
            title = title,
            modifier = modifier
        )
    }
}

object DynamicLeafProbeAutoComponent {
    @Composable
    fun create(
        json: JsonObject,
        data: Map<String, Any> = emptyMap()
    ) {
        val context = LocalContext.current

        // Parse attributes from JSON with binding support
        val title = ResourceResolver.resolveText(json, "title", data, context)

        // Build modifier
        val modifier = ModifierBuilder.buildModifier(json, data, context = context)

        LeafProbeAuto(
            title = title,
            modifier = modifier
        ) {
            // Process children
            val children = json.get("child")?.asJsonArray ?: json.get("children")?.asJsonArray
            children?.forEach { childJson ->
                if (childJson.isJsonObject) {
                    com.kotlinjsonui.dynamic.DynamicView(
                        json = childJson.asJsonObject,
                        data = data
                    )
                }
            }
        }
    }
}
