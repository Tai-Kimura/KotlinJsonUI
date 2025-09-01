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
    
    // Don't store Context as a static field to avoid memory leaks
    // Instead, pass Context when needed or use WeakReference
    
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
        
        isInitialized = true
        
        // Check if the host app is in debug mode
        val isHostAppDebug = checkHostAppDebugMode(context)
        
        // Dynamic Mode is only available when BOTH library and host app are in debug
        val isDynamicAvailable = BuildConfig.DEBUG || isHostAppDebug
        _isDynamicModeAvailable.value = isDynamicAvailable
        
        // Load saved preference only if dynamic mode is available
        if (isDynamicAvailable) {
            loadPreference(context.applicationContext)
        } else {
            Log.i(TAG, "Dynamic Mode is not available (Library: ${if (BuildConfig.DEBUG) "DEBUG" else "RELEASE"}, App: ${if (isHostAppDebug) "DEBUG" else "RELEASE"})")
            _isDynamicModeEnabled.value = false
        }
    }
    
    /**
     * Enable or disable Dynamic Mode
     * @param context Context for accessing SharedPreferences and HotLoader
     * @param enabled true to enable, false to disable
     * @return true if the operation was successful, false if Dynamic Mode is not available
     */
    fun setDynamicModeEnabled(context: Context, enabled: Boolean): Boolean {
        if (!_isDynamicModeAvailable.value) {
            Log.w(TAG, "Cannot enable Dynamic Mode - not available in current configuration")
            return false
        }
        
        if (!isInitialized) {
            Log.e(TAG, "DynamicModeManager not initialized. Call initialize() first")
            return false
        }
        
        Log.d(TAG, "Setting Dynamic Mode enabled: $enabled")
        _isDynamicModeEnabled.value = enabled
        savePreference(context.applicationContext, enabled)
        
        // Start or stop HotLoader based on the setting
        if (enabled) {
            startDynamicMode(context.applicationContext)
        } else {
            stopDynamicMode(context.applicationContext)
        }
        
        return true
    }
    
    /**
     * Toggle Dynamic Mode on/off
     * @param context Context for accessing SharedPreferences and HotLoader
     * @return the new state, or null if Dynamic Mode is not available
     */
    fun toggleDynamicMode(context: Context): Boolean? {
        if (!_isDynamicModeAvailable.value) {
            Log.w(TAG, "Cannot toggle Dynamic Mode - not available")
            return null
        }
        
        val newState = !_isDynamicModeEnabled.value
        return if (setDynamicModeEnabled(context, newState)) {
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
        return _isDynamicModeAvailable.value && _isDynamicModeEnabled.value
    }
    
    /**
     * Check if the host application is running in debug mode
     * Uses reflection to check the host app's BuildConfig.DEBUG field
     */
    private fun checkHostAppDebugMode(context: Context): Boolean {
        return try {
            // Get the host app's package name
            val packageName = context.packageName
            
            // Try to load the host app's BuildConfig class
            val buildConfigClass = Class.forName("$packageName.BuildConfig")
            
            // Get the DEBUG field
            val debugField = buildConfigClass.getField("DEBUG")
            
            // Get the value of DEBUG field
            val isDebug = debugField.getBoolean(null)
            
            Log.d(TAG, "Host app ($packageName) is in ${if (isDebug) "DEBUG" else "RELEASE"} mode")
            isDebug
        } catch (e: ClassNotFoundException) {
            Log.w(TAG, "Could not find BuildConfig for host app: ${e.message}")
            false
        } catch (e: NoSuchFieldException) {
            Log.w(TAG, "BuildConfig.DEBUG field not found: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking host app debug mode", e)
            false
        }
    }
    
    /**
     * Get configuration for Dynamic Mode
     * Returns null if not available
     */
    fun getConfiguration(): DynamicModeConfig? {
        if (!_isDynamicModeAvailable.value) {
            return null
        }
        
        return DynamicModeConfig(
            enabled = _isDynamicModeEnabled.value,
            serverUrl = getServerUrl(),
            port = getPort()
        )
    }
    
    private fun loadPreference(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean(KEY_ENABLED, false) // Default to false
        _isDynamicModeEnabled.value = enabled
        
        if (enabled) {
            startDynamicMode(context)
        }
    }
    
    private fun savePreference(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }
    
    private fun startDynamicMode(context: Context) {
        if (!_isDynamicModeAvailable.value) {
            return
        }
        
        try {
            Log.i(TAG, "Starting Dynamic Mode")
            // Use reflection to avoid compile-time dependency on DEBUG-only classes
            val hotLoaderClass = Class.forName("com.kotlinjsonui.dynamic.hotloader.HotLoader")
            
            // Get the Companion object
            val companionClass = Class.forName("com.kotlinjsonui.dynamic.hotloader.HotLoader\$Companion")
            val companionField = hotLoaderClass.getDeclaredField("Companion")
            val companionInstance = companionField.get(null)
            
            // Call getInstance on the companion object
            val getInstanceMethod = companionClass.getDeclaredMethod("getInstance", Context::class.java)
            val hotLoader = getInstanceMethod.invoke(companionInstance, context)
            
            val startMethod = hotLoaderClass.getDeclaredMethod("start")
            startMethod.invoke(hotLoader)
            
            Log.i(TAG, "Dynamic Mode started successfully")
        } catch (e: ClassNotFoundException) {
            Log.d(TAG, "HotLoader class not found - this is expected in release builds")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting Dynamic Mode", e)
        }
    }
    
    private fun stopDynamicMode(context: Context) {
        if (!_isDynamicModeAvailable.value) {
            return
        }
        
        try {
            Log.i(TAG, "Stopping Dynamic Mode")
            // Use reflection to avoid compile-time dependency on DEBUG-only classes
            val hotLoaderClass = Class.forName("com.kotlinjsonui.dynamic.hotloader.HotLoader")
            
            // Get the Companion object
            val companionClass = Class.forName("com.kotlinjsonui.dynamic.hotloader.HotLoader\$Companion")
            val companionField = hotLoaderClass.getDeclaredField("Companion")
            val companionInstance = companionField.get(null)
            
            // Call getInstance on the companion object
            val getInstanceMethod = companionClass.getDeclaredMethod("getInstance", Context::class.java)
            val hotLoader = getInstanceMethod.invoke(companionInstance, context)
            
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
     * Only works when Dynamic Mode is available
     * @param context Context for accessing SharedPreferences
     */
    fun reset(context: Context) {
        if (!_isDynamicModeAvailable.value) {
            return
        }
        
        setDynamicModeEnabled(context, false)
        val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
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
        if (!_isDynamicModeAvailable.value) {
            Log.d(TAG, "DynamicView not available in current configuration")
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
     * Returns null if Dynamic Mode is not available
     * This method uses reflection to avoid compile-time dependency on DEBUG-only classes
     */
    fun createDynamicView(layoutName: String, context: Context? = null): Any? {
        if (!_isDynamicModeAvailable.value) {
            Log.d(TAG, "DynamicView not available in current configuration")
            return null
        }
        
        try {
            val dynamicViewClass = Class.forName("com.kotlinjsonui.dynamic.DynamicView")
            val constructor = dynamicViewClass.getDeclaredConstructor(
                String::class.java,
                Context::class.java
            )
            
            return constructor.newInstance(layoutName, context)
        } catch (e: ClassNotFoundException) {
            Log.d(TAG, "DynamicView class not found - this is expected in release builds")
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Error creating DynamicView instance", e)
            return null
        }
    }
}