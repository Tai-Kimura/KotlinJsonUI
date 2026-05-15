package com.kotlinjsonui.embed

import org.junit.Assert.*
import org.junit.Test

/**
 * JVM-side tests for non-Composable Embed primitives.
 *
 * Compose-runtime behavior (per-embedId ViewModelStoreOwner isolation,
 * LocalEmbeddedScreenContext propagation, DriveEmbedInitParams effect)
 * requires Compose test infrastructure and runs as instrumented tests
 * elsewhere (see jsonui-test-runner-android driver).
 */
class EmbedContainerTest {

    // ── EmbeddedEvent ──

    @Test
    fun embeddedEvent_named_carriesNameAndPayload() {
        val event = EmbeddedEvent.Named(name = "onOrderUpdated", payload = mapOf("id" to 42))
        assertEquals("onOrderUpdated", event.name)
        assertEquals(42, event.payload["id"])
    }

    @Test
    fun embeddedEvent_named_emptyPayloadAllowed() {
        val event = EmbeddedEvent.Named(name = "onClose", payload = emptyMap())
        assertTrue(event.payload.isEmpty())
    }

    // ── EmbedNavigationMode ──

    @Test
    fun embedNavigationMode_hasDelegateAndIsolated() {
        assertEquals(setOf(EmbedNavigationMode.Delegate, EmbedNavigationMode.Isolated),
                     EmbedNavigationMode.values().toSet())
    }

    // ── EmbeddedNavigationDelegate ──

    @Test
    fun navigationDelegate_delegateMode_boundsPopAtRoot() {
        val delegate = EmbeddedNavigationDelegate(boundedAtEmbed = true)
        assertTrue("pop at embed root should be bounded",
                   delegate.shouldBoundPop(internalPushDepth = 0))
    }

    @Test
    fun navigationDelegate_delegateMode_doesNotBoundPushedScreens() {
        val delegate = EmbeddedNavigationDelegate(boundedAtEmbed = true)
        assertFalse("pop with internal push depth > 0 must NOT be bounded — it should drive the internal stack",
                    delegate.shouldBoundPop(internalPushDepth = 1))
        assertFalse(delegate.shouldBoundPop(internalPushDepth = 5))
    }

    @Test
    fun navigationDelegate_isolatedMode_neverBoundsPop() {
        val delegate = EmbeddedNavigationDelegate(boundedAtEmbed = false)
        assertFalse(delegate.shouldBoundPop(internalPushDepth = 0))
        assertFalse(delegate.shouldBoundPop(internalPushDepth = 3))
    }

    @Test
    fun navigationDelegate_defaultDepthZero() {
        val delegate = EmbeddedNavigationDelegate(boundedAtEmbed = true)
        // No-arg call → defaults internalPushDepth=0 → bounded
        assertTrue(delegate.shouldBoundPop())
    }

    // ── EmbeddedScreenContext.emit ──

    @Test
    fun emit_routesNamedEventToBridge() {
        var captured: EmbeddedEvent? = null
        val ctx = EmbeddedScreenContext(
            embedId = "detail",
            params = mapOf("orderId" to "abc"),
            navigationDelegate = EmbeddedNavigationDelegate(boundedAtEmbed = true),
            eventBridge = { captured = it }
        )
        ctx.emit("onOrderUpdated", payload = mapOf("id" to 7))

        val event = captured
        assertTrue("emit must produce a Named event", event is EmbeddedEvent.Named)
        val named = event as EmbeddedEvent.Named
        assertEquals("onOrderUpdated", named.name)
        assertEquals(7, named.payload["id"])
    }

    @Test
    fun emit_withoutBridge_isNoop() {
        // Standalone screens (not embedded) get a null bridge — must not crash.
        val ctx = EmbeddedScreenContext(
            embedId = "standalone",
            params = emptyMap(),
            navigationDelegate = EmbeddedNavigationDelegate(boundedAtEmbed = true),
            eventBridge = null
        )
        ctx.emit("onAnything")  // no exception expected
    }

    @Test
    fun emit_defaultPayloadIsEmptyMap() {
        var captured: EmbeddedEvent.Named? = null
        val ctx = EmbeddedScreenContext(
            embedId = "x",
            params = emptyMap(),
            navigationDelegate = EmbeddedNavigationDelegate(boundedAtEmbed = false),
            eventBridge = { captured = it as EmbeddedEvent.Named }
        )
        ctx.emit("onTap")
        assertNotNull(captured)
        assertTrue(captured!!.payload.isEmpty())
    }

    // ── InitParamsReceiver contract ──

    /** Test double for VMs that opt in to embed init params. */
    private class TestVm : InitParamsReceiver {
        var lastParams: Map<String, Any>? = null
        var callCount = 0
        override fun applyInitParams(params: Map<String, Any>) {
            lastParams = params
            callCount++
        }
    }

    @Test
    fun initParamsReceiver_canBeCalledRepeatedly() {
        val vm = TestVm()
        vm.applyInitParams(mapOf("orderId" to "a"))
        vm.applyInitParams(mapOf("orderId" to "b"))
        assertEquals(2, vm.callCount)
        assertEquals("b", vm.lastParams?.get("orderId"))
    }
}
