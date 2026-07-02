package com.kotlinjsonui.embed

import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
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

    // ── EmbedViewModelStoreOwner Hilt factory bridge (regression: kjui-embed-vm-store-owner-not-hilt-aware) ──
    //
    // `hiltViewModel(viewModelStoreOwner = ...)` only engages
    // HiltViewModelFactory when the owner implements
    // HasDefaultViewModelProviderFactory. Without that, @HiltViewModel VMs
    // fall through to NewInstanceFactory and crash on a missing no-arg ctor.
    // The owner forwards the PARENT's default factory + creation extras so
    // both Hilt projects (parent factory = HiltViewModelFactory) and
    // non-Hilt projects (parent factory = AndroidViewModelFactory) work
    // through the same code path.

    @Test
    fun embedViewModelStoreOwner_implementsHasDefaultViewModelProviderFactory() {
        // Documents the contract: hiltViewModel() only wraps in HiltViewModelFactory
        // when the owner IS-A HasDefaultViewModelProviderFactory. A future refactor
        // dropping the interface would break this cast at runtime AND the lower
        // test that reads defaultViewModelProviderFactory at compile time.
        val owner: Any = EmbedViewModelStoreOwner(parentFactory = null, parentExtras = CreationExtras.Empty)
        assertTrue(
            "EmbedViewModelStoreOwner must implement HasDefaultViewModelProviderFactory",
            owner is HasDefaultViewModelProviderFactory
        )
    }

    @Test
    fun embedViewModelStoreOwner_forwardsParentFactory() {
        val parentFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return Object() as T
            }
        }
        val owner = EmbedViewModelStoreOwner(parentFactory, CreationExtras.Empty)
        assertSame(
            "The parent's default factory must pass through unchanged so " +
                "hiltViewModel() can wrap it in HiltViewModelFactory",
            parentFactory,
            owner.defaultViewModelProviderFactory
        )
    }

    @Test
    fun embedViewModelStoreOwner_fallsBackToNewInstanceFactoryWhenParentHasNoDefault() {
        // Parent owners that don't implement HasDefaultViewModelProviderFactory
        // pass `null` for parentFactory. The embed owner must still satisfy
        // the HasDefaultViewModelProviderFactory contract (non-null factory).
        val owner = EmbedViewModelStoreOwner(parentFactory = null, parentExtras = CreationExtras.Empty)
        assertTrue(
            "Fallback factory must be a NewInstanceFactory so non-Hilt callers still work",
            owner.defaultViewModelProviderFactory is ViewModelProvider.NewInstanceFactory
        )
    }

    @Test
    fun embedViewModelStoreOwner_forwardsParentCreationExtras() {
        val key = object : CreationExtras.Key<String> {}
        val extras = MutableCreationExtras().apply { set(key, "from-parent") }
        val owner = EmbedViewModelStoreOwner(parentFactory = null, parentExtras = extras)
        assertEquals(
            "Parent creation extras must propagate so SavedStateHandle, ActivityRetainedComponent, etc. are reachable",
            "from-parent",
            owner.defaultViewModelCreationExtras[key]
        )
    }

    @Test
    fun embedViewModelStoreOwner_givesEachInstanceItsOwnViewModelStore() {
        // Per-embed VM isolation must NOT regress: distinct EmbedViewModelStoreOwner
        // instances need distinct stores, otherwise two Embeds of the same screen
        // in the same parent share VM state.
        val a = EmbedViewModelStoreOwner(parentFactory = null, parentExtras = CreationExtras.Empty)
        val b = EmbedViewModelStoreOwner(parentFactory = null, parentExtras = CreationExtras.Empty)
        assertNotSame(a.viewModelStore, b.viewModelStore)
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
