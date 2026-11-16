package com.kotlinjsonui.conformance

import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DynamicView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * What the Android driver finds for a decorative image — NOT part of the
 * conformance suite, and skipped unless requested:
 *
 *   adb shell am instrument -w \
 *     -e class com.kotlinjsonui.conformance.DecorativeImageProbeTest \
 *     -e decorativeImageProbe 1 \
 *     com.kotlinjsonui.conformance.test/androidx.test.runner.AndroidJUnitRunner
 *
 * The driver looks an element up by resource-id only (jsonui-test-runner
 * AssertionExecutor: visible = findObject(By.res(id)) != null, notVisible =
 * the opposite, count = findObjects(By.res(id)).size). A decorative image is
 * emitted `contentDescription = null` with its testTag, so TalkBack skips it
 * — and the node stays in UiAutomator's tree under its resource-id (measured,
 * API 35 emulator): visible, notVisible and count answer as before. This is
 * the platform where hiding an image from the screen reader does NOT change
 * what a test sees; on iOS the same image satisfies both visible and
 * notVisible (SwiftJsonUI ConformanceHost -decorativeImageProbe).
 *
 * The codegen shape (null + testTag, as kjui emits it) sits beside the old
 * shape, and the dynamic path renders the rule itself: a control keeps its
 * id, an alt reaches content-desc.
 */
@OptIn(ExperimentalComposeUiApi::class)
@RunWith(AndroidJUnit4::class)
class DecorativeImageProbeTest {

    private data class Row(val id: String, val count: Int, val desc: String?)

    /** The measured table: found by resource-id, and what TalkBack is handed. */
    private val expected = listOf(
        Row("img_null", 1, null),
        Row("img_desc", 1, "img_desc"),
        Row("dyn_decorative", 1, null),
        Row("dyn_control", 1, "dyn_control"),
        Row("dyn_labelled", 1, "Sample"),
        Row("img_absent", 0, null),
    )

    private val layout = """
    {"type": "View", "orientation": "vertical", "child": [
      {"type": "Image", "id": "dyn_decorative", "srcName": "conformance_sample", "width": 40, "height": 40},
      {"type": "View", "onClick": "@{onProbe}", "child": [
        {"type": "Image", "id": "dyn_control", "srcName": "conformance_sample", "width": 40, "height": 40}
      ]},
      {"type": "Image", "id": "dyn_labelled", "srcName": "conformance_sample", "width": 40, "height": 40, "alt": "Sample"}
    ]}
    """

    @Before
    fun skipUnlessRequested() {
        val enabled = InstrumentationRegistry.getArguments().getString("decorativeImageProbe") == "1"
        Assume.assumeTrue("set -e decorativeImageProbe 1 to run the decorative-image probe", enabled)
    }

    @Test
    fun whatTheDriverSeesOfEachSpelling() {
        val scenario = ActivityScenario.launch(FixtureHostActivity::class.java)
        scenario.onActivity { activity ->
            activity.setContent {
                Column(Modifier.semantics { testTagsAsResourceId = true }) {
                    Image(
                        painter = painterResource(R.drawable.conformance_sample),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp).testTag("img_null").semantics { testTagsAsResourceId = true }
                    )
                    Image(
                        painter = painterResource(R.drawable.conformance_sample),
                        contentDescription = "img_desc",
                        modifier = Modifier.size(40.dp).testTag("img_desc").semantics { testTagsAsResourceId = true }
                    )
                    DynamicView(
                        json = JsonParser.parseString(layout).asJsonObject,
                        data = mapOf("onProbe" to { })
                    )
                }
            }
        }
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        assertTrue("probe did not start", device.wait(Until.hasObject(By.res("img_desc")), 10_000))

        for (row in expected) {
            val all = device.findObjects(By.res(row.id))
            val one = all.firstOrNull()
            println("DECO_ANDROID id=${row.id} count=${all.size} visible=${one != null} " +
                "notVisible=${one == null} desc='${one?.contentDescription}'")
            assertEquals("${row.id} count", row.count, all.size)
            assertEquals("${row.id} content-desc", row.desc, one?.contentDescription)
        }
        scenario.close()
    }
}
