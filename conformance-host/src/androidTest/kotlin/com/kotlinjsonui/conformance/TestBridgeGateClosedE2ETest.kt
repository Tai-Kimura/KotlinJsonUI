package com.kotlinjsonui.conformance

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jsonui.testrunner.models.TestStep
import kotlinx.serialization.json.JsonPrimitive
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeFalse
import org.junit.Test
import org.junit.runner.RunWith

/**
 * initialState through the driver's run path, in the gateClosed build type
 * (not debuggable, no com.kotlinjsonui.SCREEN_MARKER meta-data, as a shipped
 * app): the screen applies nothing and publishes nothing, so the driver
 * reports no receipt — `setup`, not a case run against default values.
 *
 * Runs only in that build: `-PconformanceTestBuildType=gateClosed
 * :conformance-host:connectedGateClosedAndroidTest` with this class
 * (jsonui-cli's kjui_device_tests.sh does, as a second invocation). The
 * debug invocation leaves it out. See [TestBridgeE2E].
 */
@RunWith(AndroidJUnit4::class)
class TestBridgeGateClosedE2ETest {

    @After
    fun blank() = TestBridgeE2E.reset()

    @Test
    fun withTheGateClosedNoReceiptComesAndTheCaseFailsSetup() {
        assumeFalse("the gateClosed build only (-PconformanceTestBuildType=gateClosed)", TestBridgeE2E.debuggable)
        val started = System.currentTimeMillis()
        val suite = TestBridgeE2E.run(
            TestBridgeE2E.screenTest(
                mapOf("tbEmail" to JsonPrimitive("a@example.com")),
                listOf(TestStep(assert = "text", id = "label_email", equals = JsonPrimitive("unset")))
            )
        )
        val row = suite.results.single()
        assertFalse(row.passed)
        assertEquals("setup", row.failureReason)
        assertTrue(
            row.error,
            row.error!!.contains(
                "initialState for '${TestBridgeE2E.screenId}' was not confirmed by the app within ${TestBridgeE2E.SCREEN_READY_MS}ms"
            )
        )
        assertTrue("waited the screen-ready time", System.currentTimeMillis() - started >= TestBridgeE2E.SCREEN_READY_MS)
    }
}
