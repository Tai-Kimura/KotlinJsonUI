package com.kotlinjsonui.conformance

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jsonui.testrunner.models.TestStep
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * initialState and `state` through the driver's run path, in a build with the
 * screen-marker gate open (debuggable): an accepted payload is confirmed and
 * the case runs; a rejected key fails `setup` with the key and reason. The
 * gate-closed arm is TestBridgeGateClosedE2ETest, run in the gateClosed
 * build. See [TestBridgeE2E].
 */
@RunWith(AndroidJUnit4::class)
class TestBridgeRunnerE2ETest {

    @After
    fun blank() = TestBridgeE2E.reset()

    @Test
    fun anAcceptedPayloadIsConfirmedAndStateReadsTheBridge() {
        assumeTrue("a debuggable build (the gate open)", TestBridgeE2E.debuggable)
        val suite = TestBridgeE2E.run(
            TestBridgeE2E.screenTest(
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
        assumeTrue("a debuggable build (the gate open)", TestBridgeE2E.debuggable)
        val suite = TestBridgeE2E.run(
            TestBridgeE2E.screenTest(
                mapOf("tbEmail" to JsonPrimitive("x"), "tbCount" to JsonPrimitive(1.5)),
                listOf(TestStep(assert = "state", path = "tbEmail", equals = JsonPrimitive("x")))
            )
        )
        val row = suite.results.single()
        assertFalse(row.passed)
        assertEquals("setup", row.failureReason)
        assertTrue(
            row.error,
            row.error!!.contains(
                "initialState for '${TestBridgeE2E.screenId}' was rejected by the app: 'tbCount': type mismatch: expected Int"
            )
        )
    }
}
