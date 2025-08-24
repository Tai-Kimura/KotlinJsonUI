package com.kotlinjsonui.core

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kotlinjsonui.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manager for controlling Dynamic Mode (HotLoader) functionality
 * Dynamic Mode is only available in DEBUG builds
 * In RELEASE builds, this class exists but Dynamic Mode is always disabled
 */
object DynamicModeManager {
    private const val TAG = "DynamicModeManager"
    private const val PREF_NAME = "kjui_dynamic_mode"
    private const val KEY_ENABLED = "dynamic_mode_enabled"
    
    private val _isDynamicModeEnabled = MutableStateFlow(false)
    val isDynamicModeEnabled: StateFlow<Boolean> = _isDynamicModeEnabled.asStateFlow()
    
    private val _isDynamicModeAvailable = MutableStateFlow(BuildConfig.DEBUG)
    val isDynamicModeAvailable: StateFlow<Boolean> = _isDynamicModeAvailable.asStateFlow()
    
    private var context: Context? = null
    
    /**
     * Check if the manager has been initialized
     */
    var isInitialized = false
        private set
    
    /**
     * Initialize the Dynamic Mode Manager
     * Must be called before using Dynamic Mode features
     */
    fun initialize(context: Context) {
        if (isInitialized) {
            Log.d(TAG, "Already initialized")
            return
        }
        
        this.context = context.applicationContext
        isInitialized = true
        
        // Load saved preference only in DEBUG builds
        if (BuildConfig.DEBUG) {
            loadPreference()
        } else {
            Log.i(TAG, "Dynamic Mode is not available in release builds")
            _isDynamicModeEnabled.value = false
            _isDynamicModeAvailable.value = false
        }
    }
    
    /**
     * Enable or disable Dynamic Mode
     * @param enabled true to enable, false to disable
     * @return true if the operation was successful, false if Dynamic Mode is not available
     */
    fun setDynamicModeEnabled(enabled: Boolean): Boolean {
        if (!BuildConfig.DEBUG) {
            Log.w(TAG, "Cannot enable Dynamic Mode in release build")
            return false
        }
        
        if (!isInitialized) {
            Log.e(TAG, "DynamicModeManager not initialized. Call initialize() first")
            return false
        }
        
        Log.d(TAG, "Setting Dynamic Mode enabled: $enabled")
        _isDynamicModeEnabled.value = enabled
        savePreference(enabled)
        
        // Start or stop HotLoader based on the setting
        if (enabled) {
            startDynamicMode()
        } else {
            stopDynamicMode()
        }
        
        return true
    }
    
    /**
     * Toggle Dynamic Mode on/off
     * @return the new state, or null if Dynamic Mode is not available
     */
    fun toggleDynamicMode(): Boolean? {
        if (!BuildConfig.DEBUG) {
            Log.w(TAG, "Cannot toggle Dynamic Mode in release build")
            return null
        }
        
        val newState = !_isDynamicModeEnabled.value
        return if (setDynamicModeEnabled(newState)) {
            newState
        } else {
            null
        }
    }
    
    /**
     * Check if Dynamic Mode is currently active
     * This checks both availability (DEBUG build) and enabled state
     */
    fun isActive(): Boolean {
        return BuildConfig.DEBUG && _isDynamicModeEnabled.value
    }
    
    /**
     * Get configuration for Dynamic Mode
     * Returns null if not in DEBUG build
     */
    fun getConfiguration(): DynamicModeConfig? {
        if (!BuildConfig.DEBUG) {
            return null
        }
        
        return DynamicModeConfig(
            enabled = _isDynamicModeEnabled.value,
            serverUrl = getServerUrl(),
            port = getPort()
        )
    }
    
    private fun loadPreference() {
        context?.let {
            val prefs = it.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean(KEY_ENABLED, false) // Default to false
            _isDynamicModeEnabled.value = enabled
            
            if (enabled) {
                startDynamicMode()
            }
        }
    }
    
    private fun savePreference(enabled: Boolean) {
        context?.let {
            val prefs = it.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
        }
    }
    
    private fun startDynamicMode() {
        if (!BuildConfig.DEBUG) {
            return
        }
        
        try {
            Log.i(TAG, "Starting Dynamic Mode")
            // Use reflection to avoid compile-time dependency on DEBUG-only classes
            val hotLoaderClass = Class.forName("com.kotlinjsonui.dynamic.hotloader.HotLoader")
            val getInstanceMethod = hotLoaderClass.getDeclaredMethod("getInstance", Context::class.java)
            val hotLoader = getInstanceMethod.invoke(null, context)
            
            val startMethod = hotLoaderClass.getDeclaredMethod("start")
            startMethod.invoke(hotLoader)
            
            Log.i(TAG, "Dynamic Mode started successfully")
        } catch (e: ClassNotFoundException) {
            Log.d(TAG, "HotLoader class not found - this is expected in release builds")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting Dynamic Mode", e)
        }
    }
    
    private fun stopDynamicMode() {
        if (!BuildConfig.DEBUG) {
            return
        }
        
        try {
            Log.i(TAG, "Stopping Dynamic Mode")
            // Use reflection to avoid compile-time dependency on DEBUG-only classes
            val hotLoaderClass = Class.forName("com.kotlinjsonui.dynamic.hotloader.HotLoader")
            val getInstanceMethod = hotLoaderClass.getDeclaredMethod("getInstance", Context::class.java)
            val hotLoader = getInstanceMethod.invoke(null, context)
            
            val stopMethod = hotLoaderClass.getDeclaredMethod("stop")
            stopMethod.invoke(hotLoader)
            
            Log.i(TAG, "Dynamic Mode stopped successfully")
        } catch (e: ClassNotFoundException) {
            Log.d(TAG, "HotLoader class not found - this is expected in release builds")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping Dynamic Mode", e)
        }
    }
    
    private fun getServerUrl(): String {
        // TODO: Load from configuration
        return "ws://10.0.2.2:8081"
    }
    
    private fun getPort(): Int {
        // TODO: Load from configuration
        return 8081
    }
    
    /**
     * Configuration data class for Dynamic Mode
     */
    data class DynamicModeConfig(
        val enabled: Boolean,
        val serverUrl: String,
        val port: Int
    )
    
    /**
     * Reset all settings to defaults
     * Only works in DEBUG builds
     */
    fun reset() {
        if (!BuildConfig.DEBUG) {
            return
        }
        
        setDynamicModeEnabled(false)
        context?.let {
            val prefs = it.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
        }
    }
    
    /**
     * Create a DynamicView component
     * Returns null in release builds or if Dynamic Mode is not available
     * This method uses reflection to avoid compile-time dependency on DEBUG-only classes
     */
    @Composable
    fun DynamicView(
        layoutName: String,
        modifier: Modifier = Modifier,
        onError: ((String) -> Unit)? = null,
        onLoading: @Composable () -> Unit = {},
        content: @Composable (String) -> Unit = {}
    ) {
        if (!BuildConfig.DEBUG) {
            Log.d(TAG, "DynamicView not available in release build")
            return
        }
        
        if (!isActive()) {
            Log.d(TAG, "Dynamic Mode is not active")
            return
        }
        
        try {
            // Use reflection to call the DynamicView composable
            val dynamicViewClass = Class.forName("com.kotlinjsonui.dynamic.DynamicViewKt")
            val method = dynamicViewClass.getDeclaredMethod(
                "DynamicView",
                String::class.java,
                Modifier::class.java,
                Function1::class.java, // onError
                Function0::class.java, // onLoading  
                Function1::class.java  // content
            )
            
            method.invoke(
                null,
                layoutName,
                modifier,
                onError,
                onLoading,
                content
            )
        } catch (e: ClassNotFoundException) {
            Log.d(TAG, "DynamicView class not found - this is expected in release builds")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating DynamicView", e)
            onError?.invoke("Failed to create DynamicView: ${e.message}")
        }
    }
    
    /**
     * Create a DynamicView instance (non-composable)
     * Returns null in release builds
     * This method uses reflection to avoid compile-time dependency on DEBUG-only classes
     */
    fun createDynamicView(layoutName: String, context: Context? = null): Any? {
        if (!BuildConfig.DEBUG) {
            Log.d(TAG, "DynamicView not available in release build")
            return null
        }
        
        try {
            val dynamicViewClass = Class.forName("com.kotlinjsonui.dynamic.DynamicView")
            val constructor = dynamicViewClass.getDeclaredConstructor(
                String::class.java,
                Context::class.java
            )
            
            return constructor.newInstance(layoutName, context ?: this.context)
        } catch (e: ClassNotFoundException) {
            Log.d(TAG, "DynamicView class not found - this is expected in release builds")
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error creating DynamicView instance", e)
            return null
        }
    }
}