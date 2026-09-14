package com.kotlinjsonui.core

import com.kotlinjsonui.BuildConfig
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DynamicModeManagerTest {

    /**
     * Boundary control for the AGP 9 migration.
     *
     * AGP 9 turns the release unit-test component off by default, so
     * `testReleaseUnitTest` disappears unless it is switched back on in
     * build.gradle.kts. The tempting fix is to point CI at
     * `testDebugUnitTest` instead -- and nothing here would have gone red if
     * we had, because no arm read this seed before. That silence is the
     * hazard: `_isDynamicModeAvailable` is seeded from `BuildConfig.DEBUG`
     * (DynamicModeManager.kt:28), so the swap flips it false -> true and the
     * suite quietly starts describing a variant that is never shipped.
     *
     * This arm pins the wiring rather than the literal, so it is honest in
     * both variants, and prints the side it ran on so the two sides can be
     * read off one table:
     *
     *   release  BuildConfig.DEBUG=false  seed=false
     *   debug    BuildConfig.DEBUG=true   seed=true
     *
     * A hard-coded `false` here would pass for the wrong reason in release
     * and force a red in every local debug run, which is why the assertion
     * is the equality and not the value.
     */
    @Test
    fun `dynamic mode availability is seeded from the build variant`() {
        val seed = DynamicModeManager.isDynamicModeAvailable.value
        println(
            "variant-boundary: BuildConfig.DEBUG=${BuildConfig.DEBUG} " +
                "isDynamicModeAvailable=$seed"
        )
        assertEquals(
            "the seed must track BuildConfig.DEBUG, not a hard-coded literal",
            BuildConfig.DEBUG,
            seed
        )
    }

    @Test
    fun `DynamicModeConfig holds correct values`() {
        val config = DynamicModeManager.DynamicModeConfig(
            enabled = true,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )

        assertTrue(config.enabled)
        assertEquals("ws://localhost:8081", config.serverUrl)
        assertEquals(8081, config.port)
    }

    @Test
    fun `DynamicModeConfig equals works correctly`() {
        val config1 = DynamicModeManager.DynamicModeConfig(
            enabled = true,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )
        val config2 = DynamicModeManager.DynamicModeConfig(
            enabled = true,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )
        val config3 = DynamicModeManager.DynamicModeConfig(
            enabled = false,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )

        assertEquals(config1, config2)
        assertNotEquals(config1, config3)
    }

    @Test
    fun `DynamicModeConfig hashCode is consistent`() {
        val config1 = DynamicModeManager.DynamicModeConfig(
            enabled = true,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )
        val config2 = DynamicModeManager.DynamicModeConfig(
            enabled = true,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )

        assertEquals(config1.hashCode(), config2.hashCode())
    }

    @Test
    fun `DynamicModeConfig copy works correctly`() {
        val original = DynamicModeManager.DynamicModeConfig(
            enabled = true,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )
        val copied = original.copy(enabled = false)

        assertTrue(original.enabled)
        assertFalse(copied.enabled)
        assertEquals(original.serverUrl, copied.serverUrl)
        assertEquals(original.port, copied.port)
    }

    @Test
    fun `DynamicModeConfig toString contains all fields`() {
        val config = DynamicModeManager.DynamicModeConfig(
            enabled = true,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )
        val str = config.toString()

        assertTrue(str.contains("enabled=true"))
        assertTrue(str.contains("serverUrl=ws://localhost:8081"))
        assertTrue(str.contains("port=8081"))
    }

    @Test
    fun `DynamicModeConfig destructuring works`() {
        val config = DynamicModeManager.DynamicModeConfig(
            enabled = true,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )
        val (enabled, serverUrl, port) = config

        assertTrue(enabled)
        assertEquals("ws://localhost:8081", serverUrl)
        assertEquals(8081, port)
    }

    @Test
    fun `isActive returns false when not initialized`() {
        // Without initialization, isActive should return false
        // Note: This test may need adjustment based on actual state
        val result = DynamicModeManager.isActive()
        // In unit tests (not Android tests), dynamic mode is typically not available
        assertFalse(result)
    }
}
