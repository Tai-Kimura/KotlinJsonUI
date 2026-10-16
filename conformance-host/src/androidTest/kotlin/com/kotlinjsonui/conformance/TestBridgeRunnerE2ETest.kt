package com.kotlinjsonui.conformance

import android.content.pm.ApplicationInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.jsonui.testrunner.models.InitialState
import com.jsonui.testrunner.models.ScreenTest
import com.jsonui.testrunner.models.TestCase
import com.jsonui.testrunner.models.TestMetadata
import com.jsonui.testrunner.models.TestSource
import com.jsonui.testrunner.models.TestStep
import com.jsonui.testrunner.runner.JsonUITestRunner
import com.jsonui.testrunner.runner.TestRunnerConfig
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * initialState and `state` through the DRIVER's run path (jsonui-test-runner
 * spec §10), end to end: JsonUITestRunner.runScreenTest relaunches the app
 * with JSONUI_TEST_INITIAL_STATE on the intent, KotlinJsonUI's bridge on the
 * screen applies it and publishes the receipt on the screen marker, the
 * driver waits for the receipt, and `state` reads the bridge with no
 * provider. The screen is the TestBridge/initialState__applied fixture,
 * drawn by this host's dynamic face (its bridge is TestBridgeHost's).
 *
 * Three outcomes, two builds:
 * - debuggable (the default build): a payload the screen accepts is
 *   confirmed and the case runs; a payload with rejections fails `setup`
 *   with each key and reason.
 * - gate closed (`-PconformanceTestBuildType=gateClosed`: not debuggable, no
 *   com.kotlinjsonui.SCREEN_MARKER meta-data): the screen applies nothing and
 *   publishes nothing, so the driver reports no receipt — `setup`, not a case
 *   run against default values.
 * Each test assumes its build; the other build's tests show as skipped.
 */
@RunWith(AndroidJUnit4::class)
class TestBridgeRunnerE2ETest {

    private val fixtureId = "TestBridge/initialState__applied"
    private val screenId = TestBridgeHost.dynamicScreenId(fixtureId)

    private val debuggable: Boolean
        get() = (InstrumentationRegistry.getInstrumentation().targetContext.applicationInfo.flags and
            ApplicationInfo.FLAG_DEBUGGABLE) != 0

    private fun runner() = JsonUITestRunner(
        TestRunnerConfig(
            defaultTimeout = 5000L,
            screenshotOnFailure = false,
            verifyScreenTransitions = false,
            screenReadyTimeout = 8000L
        )
    )

    private fun screenTest(values: Map<String, JsonElement>, steps: List<TestStep>) = ScreenTest(
        type = "screen",
        // The screen under test's id is the layout basename: this host's id.
        source = TestSource(layout = "Layouts/$screenId.json"),
        metadata = TestMetadata(name = "TestBridgeE2E"),
        initialState = InitialState(viewModel = values),
        cases = listOf(TestCase(name = "applied", steps = steps))
    )

    private fun run(test: ScreenTest) = run {
        // The relaunch starts the launcher activity with no fixture extra; the
        // host keeps showing the fixture set here (same process).
        FixtureHost.hostMode.value = "dynamic"
        FixtureHost.show(fixtureId)
        runner().runScreenTest(test)
    }

    @After
    fun blank() = FixtureHost.show(null)

    @Test
    fun anAcceptedPayloadIsConfirmedAndStateReadsTheBridge() {
        assumeTrue("debuggable build only", debuggable)
        val suite = run(
            screenTest(
                mapOf(
                    "tbEmail" to JsonPrimitive("a@example.com"),
                    "tbCount" to JsonPrimitive(3),
                    "tbAgreed" to JsonPrimitive(true),
                    "tbNickname" to JsonNull
                ),
                listOf(
                    TestStep(assert = "text", id = "label_email", equals = JsonPrimitive("a@example.com")),
                    TestStep(assert = "state", path = "tbEmail", equals = JsonPrimitive("a@example.com")),
                    TestStep(assert = "state", path = "tbCount", equals = JsonPrimitive(3)),
                    TestStep(assert = "state", path = "tbAgreed", equals = JsonPrimitive(true)),
                    TestStep(assert = "state", path = "tbNickname", equals = JsonNull)
                )
            )
        )
        val row = suite.results.single()
        assertTrue("${row.failureReason}: ${row.error}", row.passed)
    }

    @Test
    fun aRejectedKeyFailsSetupWithItsReason() {
        assumeTrue("debuggable build only", debuggable)
        val suite = run(
            screenTest(
                mapOf("tbEmail" to JsonPrimitive("x"), "tbCount" to JsonPrimitive(1.5)),
                listOf(TestStep(assert = "state", path = "tbEmail", equals = JsonPrimitive("x")))
            )
        )
        val row = suite.results.single()
        assertFalse(row.passed)
        assertEquals("setup", row.failureReason)
        assertTrue(
            row.error,
            row.error!!.contains("initialState for '$screenId' was rejected by the app: 'tbCount': type mismatch: expected Int")
        )
    }

    @Test
    fun withTheGateClosedNoReceiptComesAndTheCaseFailsSetup() {
        assumeFalse("gate-closed build only (-PconformanceTestBuildType=gateClosed)", debuggable)
        val started = System.currentTimeMillis()
        val suite = run(
            screenTest(
                mapOf("tbEmail" to JsonPrimitive("a@example.com")),
                listOf(TestStep(assert = "text", id = "label_email", equals = JsonPrimitive("unset")))
            )
        )
        val row = suite.results.single()
        assertFalse(row.passed)
        assertEquals("setup", row.failureReason)
        assertTrue(row.error, row.error!!.contains("initialState for '$screenId' was not confirmed by the app within 8000ms"))
        assertTrue("waited the screen-ready time", System.currentTimeMillis() - started >= 8000L)
    }
}
