package com.kotlinjsonui.core

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DynamicModeManagerTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun dynamicModeManager_initialize() {
        DynamicModeManager.initialize(context)
        assertTrue(DynamicModeManager.isInitialized)
    }

    @Test
    fun dynamicModeManager_isDynamicModeAvailable() {
        DynamicModeManager.initialize(context)
        // In test environment, may or may not be available depending on build type
        assertNotNull(DynamicModeManager.isDynamicModeAvailable.value)
    }

    @Test
    fun dynamicModeManager_isDynamicModeEnabled() {
        DynamicModeManager.initialize(context)
        assertNotNull(DynamicModeManager.isDynamicModeEnabled.value)
    }

    @Test
    fun dynamicModeManager_isActive() {
        DynamicModeManager.initialize(context)
        // isActive should return a boolean
        val isActive = DynamicModeManager.isActive()
        assertNotNull(isActive)
    }

    @Test
    fun dynamicModeManager_getConfiguration() {
        DynamicModeManager.initialize(context)
        val config = DynamicModeManager.getConfiguration()
        // May be null in release builds
        if (DynamicModeManager.isDynamicModeAvailable.value) {
            assertNotNull(config)
            assertNotNull(config?.enabled)
            assertNotNull(config?.serverUrl)
            assertNotNull(config?.port)
        }
    }

    @Test
    fun dynamicModeManager_setDynamicModeEnabled() {
        DynamicModeManager.initialize(context)
        if (DynamicModeManager.isDynamicModeAvailable.value) {
            val result = DynamicModeManager.setDynamicModeEnabled(context, true)
            assertTrue(result)
        }
    }

    @Test
    fun dynamicModeManager_toggleDynamicMode() {
        DynamicModeManager.initialize(context)
        if (DynamicModeManager.isDynamicModeAvailable.value) {
            val result = DynamicModeManager.toggleDynamicMode(context)
            assertNotNull(result)
        }
    }

    @Test
    fun dynamicModeManager_reset() {
        DynamicModeManager.initialize(context)
        DynamicModeManager.reset(context)
        // After reset, dynamic mode should be disabled
        assertFalse(DynamicModeManager.isDynamicModeEnabled.value)
    }

    @Test
    fun dynamicModeManager_createDynamicView() {
        DynamicModeManager.initialize(context)
        // In test environment, this may return null
        val view = DynamicModeManager.createDynamicView("test_layout", context)
        // May be null if dynamic module is not available
    }

    @Test
    fun dynamicModeConfig_dataClass() {
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
    fun dynamicModeConfig_equality() {
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
        assertEquals(config1, config2)
    }

    @Test
    fun dynamicModeConfig_copy() {
        val config = DynamicModeManager.DynamicModeConfig(
            enabled = true,
            serverUrl = "ws://localhost:8081",
            port = 8081
        )
        val copied = config.copy(enabled = false)
        assertFalse(copied.enabled)
        assertEquals("ws://localhost:8081", copied.serverUrl)
    }
}
