package com.kotlinjsonui.core

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class DynamicModeManagerTest {

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
