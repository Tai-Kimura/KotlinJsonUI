package com.kotlinjsonui.embed

import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Double-drive guard for [DriveEmbedInitParams] (renderer-ssot-15,
 * 2.13.0): during the generated-wiring migration, generated screen
 * composables AND legacy manual consumer wrappers may BOTH call
 * DriveEmbedInitParams for the same VM. The guard suppresses the
 * consecutive apply of identical params to the same VM instance while
 * still letting changed params through.
 */
class EmbedInitParamsGuardTest {

    private class TestVm : InitParamsReceiver {
        var callCount = 0
        override fun applyInitParams(params: Map<String, Any>) {
            callCount++
        }
    }

    @Before
    fun clearGuard() = EmbedInitParamsGuard.reset()

    @After
    fun clearGuardAfter() = EmbedInitParamsGuard.reset()

    @Test
    fun sameParamsTwice_appliedOnce() {
        val vm = TestVm()
        val params = mapOf("orderId" to "a", "count" to 5)
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, params))
        assertFalse("consecutive apply of the SAME params must be suppressed",
                    EmbedInitParamsGuard.shouldApply(vm, params))
    }

    @Test
    fun equalContentDifferentMapInstance_suppressed() {
        // Fingerprint is the params CONTENT, not the map instance —
        // generated wiring and manual wiring build separate maps.
        val vm = TestVm()
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, mapOf("orderId" to "a")))
        assertFalse(EmbedInitParamsGuard.shouldApply(vm, mapOf("orderId" to "a")))
    }

    @Test
    fun changedParams_appliedAgain() {
        val vm = TestVm()
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, mapOf("orderId" to "a")))
        assertTrue("changed params must apply again",
                   EmbedInitParamsGuard.shouldApply(vm, mapOf("orderId" to "b")))
        assertFalse(EmbedInitParamsGuard.shouldApply(vm, mapOf("orderId" to "b")))
    }

    @Test
    fun alternatingParams_eachChangeApplies() {
        // A → B → A: each transition IS a change; only consecutive
        // repeats are suppressed.
        val vm = TestVm()
        val a = mapOf("id" to "a")
        val b = mapOf("id" to "b")
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, a))
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, b))
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, a))
    }

    @Test
    fun distinctVmInstances_trackedIndependently() {
        val vm1 = TestVm()
        val vm2 = TestVm()
        val params = mapOf("orderId" to "a")
        assertTrue(EmbedInitParamsGuard.shouldApply(vm1, params))
        assertTrue("a different VM instance must get its own first apply",
                   EmbedInitParamsGuard.shouldApply(vm2, params))
        assertFalse(EmbedInitParamsGuard.shouldApply(vm1, params))
        assertFalse(EmbedInitParamsGuard.shouldApply(vm2, params))
    }

    @Test
    fun nestedParams_comparedByContent() {
        val vm = TestVm()
        val nested1 = mapOf("profile" to mapOf("name" to "Ada"))
        val nested2 = mapOf("profile" to mapOf("name" to "Ada"))
        val changed = mapOf("profile" to mapOf("name" to "Grace"))
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, nested1))
        assertFalse(EmbedInitParamsGuard.shouldApply(vm, nested2))
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, changed))
    }

    @Test
    fun reset_forgetsFingerprints() {
        val vm = TestVm()
        val params = mapOf("orderId" to "a")
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, params))
        EmbedInitParamsGuard.reset()
        assertTrue(EmbedInitParamsGuard.shouldApply(vm, params))
    }
}
