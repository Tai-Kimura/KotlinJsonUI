package com.kotlinjsonui.conformance

import android.content.pm.ApplicationInfo
import androidx.test.platform.app.InstrumentationRegistry
import com.jsonui.testrunner.models.InitialState
import com.jsonui.testrunner.models.ScreenTest
import com.jsonui.testrunner.models.TestCase
import com.jsonui.testrunner.models.TestMetadata
import com.jsonui.testrunner.models.TestSource
import com.jsonui.testrunner.models.TestStep
import com.jsonui.testrunner.models.TestSuiteResult
import com.jsonui.testrunner.runner.JsonUITestRunner
import com.jsonui.testrunner.runner.TestRunnerConfig
import kotlinx.serialization.json.JsonElement

/**
 * What TestBridgeRunnerE2ETest and TestBridgeGateClosedE2ETest share: the
 * driver's own run path (jsonui-test-runner spec §10) — runScreenTest
 * relaunches with JSONUI_TEST_INITIAL_STATE on the intent, KotlinJsonUI's
 * bridge on the screen applies it and publishes the receipt on the screen
 * marker, the driver waits for it, and `state` reads the bridge with no
 * provider — against this host's dynamic face.
 *
 * The screen is supplied here ([FixtureHost.inlineFixtures]): the layout and
 * the testBridge fields of TestBridge/initialState__applied, written out. The
 * synced fixtures are not used, because the job that runs these tests
 * (jsonui-cli conformance-mobile, android-library-tests) builds a clean
 * checkout, where src/main/assets/conformance/ — gitignored — is empty; read
 * from there the screen drew nothing, and no receipt came (run 38889760392).
 */
object TestBridgeE2E {
    const val FIXTURE_ID = "e2e/TestBridge"
    val screenId: String = TestBridgeHost.dynamicScreenId(FIXTURE_ID)

    private const val LAYOUT = """
        {
          "type": "View", "id": "root", "width": "matchParent", "height": "matchParent",
          "orientation": "vertical",
          "data": [
            {"name": "tbEmail", "class": "String", "defaultValue": "unset"},
            {"name": "tbCount", "class": "Int", "defaultValue": 0},
            {"name": "tbAgreed", "class": "Bool", "defaultValue": false},
            {"name": "tbNickname", "class": "String?", "defaultValue": "nick"}
          ],
          "child": [
            {"type": "Label", "id": "label_email", "width": "wrapContent", "height": "wrapContent",
             "text": "@{tbEmail}", "fontColor": "#000000"}
          ]
        }
    """

    private val FIELDS = listOf(
        TestBridgeHost.Field("tbEmail", "String", "\"unset\""),
        TestBridgeHost.Field("tbCount", "Int", "0"),
        TestBridgeHost.Field("tbAgreed", "Bool", "false"),
        TestBridgeHost.Field("tbNickname", "String?", "\"nick\"")
    )

    val debuggable: Boolean
        get() = (InstrumentationRegistry.getInstrumentation().targetContext.applicationInfo.flags and
            ApplicationInfo.FLAG_DEBUGGABLE) != 0

    const val SCREEN_READY_MS = 8000L

    fun screenTest(values: Map<String, JsonElement>, steps: List<TestStep>) = ScreenTest(
        type = "screen",
        // The screen under test's id is the layout basename: this host's id.
        source = TestSource(layout = "Layouts/$screenId.json"),
        metadata = TestMetadata(name = "TestBridgeE2E"),
        initialState = InitialState(viewModel = values),
        cases = listOf(TestCase(name = "applied", steps = steps))
    )

    /**
     * The relaunch starts the launcher activity with no fixture extra; the
     * host keeps showing the fixture set here (same process).
     */
    fun run(test: ScreenTest): TestSuiteResult {
        FixtureHost.inlineFixtures[FIXTURE_ID] = InlineFixture(LAYOUT, FIELDS)
        FixtureHost.hostMode.value = "dynamic"
        FixtureHost.show(FIXTURE_ID)
        return JsonUITestRunner(
            TestRunnerConfig(
                defaultTimeout = 5000L,
                screenshotOnFailure = false,
                verifyScreenTransitions = false,
                screenReadyTimeout = SCREEN_READY_MS
            )
        ).runScreenTest(test)
    }

    fun reset() {
        FixtureHost.show(null)
        FixtureHost.inlineFixtures.remove(FIXTURE_ID)
    }
}
