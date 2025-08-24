package com.kotlinjsonui.dynamic

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.util.Log
import com.kotlinjsonui.dynamic.hotloader.HotLoader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

/**
 * DynamicView - A view that can be updated in real-time from JSON files
 * Only available in DEBUG builds
 */
class DynamicView(
    private val layoutName: String,
    private val context: Context? = null
) {
    companion object {
        private const val TAG = "DynamicView"
    }
    
    private val _layoutJson = MutableStateFlow<String?>(null)
    val layoutJson: StateFlow<String?> = _layoutJson
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private var hotLoader: HotLoader? = null
    
    init {
        Log.d(TAG, "Initializing DynamicView with layout: $layoutName")
        
        // Load initial layout from assets or cache
        loadInitialLayout()
        
        // Setup HotLoader listener if context is available
        context?.let {
            setupHotLoader(it)
        }
    }
    
    private fun loadInitialLayout() {
        try {
            // TODO: Load from assets/Layouts/{layoutName}.json
            // For now, just set loading to false
            _isLoading.value = false
            Log.d(TAG, "Initial layout loading placeholder for: $layoutName")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading initial layout", e)
            _error.value = e.message
            _isLoading.value = false
        }
    }
    
    private fun setupHotLoader(context: Context) {
        try {
            hotLoader = HotLoader.getInstance(context)
            
            // Check for cached layout first
            val cachedLayout = hotLoader?.getCachedLayout(layoutName)
            if (cachedLayout != null) {
                _layoutJson.value = cachedLayout
                Log.d(TAG, "Loaded cached layout for: $layoutName")
            }
            
            // Add listener for updates
            hotLoader?.addListener(object : HotLoader.HotLoaderListener {
                override fun onConnected() {
                    Log.d(TAG, "HotLoader connected")
                }
                
                override fun onDisconnected() {
                    Log.d(TAG, "HotLoader disconnected")
                }
                
                override fun onLayoutUpdated(updatedLayoutName: String, content: String) {
                    if (updatedLayoutName == layoutName) {
                        Log.d(TAG, "Layout updated: $layoutName")
                        _layoutJson.value = content
                    }
                }
                
                override fun onLayoutAdded(addedLayoutName: String) {
                    Log.d(TAG, "Layout added: $addedLayoutName")
                }
                
                override fun onLayoutRemoved(removedLayoutName: String) {
                    if (removedLayoutName == layoutName) {
                        Log.w(TAG, "Current layout removed: $layoutName")
                        _error.value = "Layout file was removed"
                    }
                }
                
                override fun onError(error: Throwable) {
                    Log.e(TAG, "HotLoader error", error)
                    _error.value = error.message
                }
            })
            
            // Start HotLoader if not already running
            hotLoader?.start()
            
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up HotLoader", e)
            _error.value = e.message
        }
    }
    
    fun updateLayout(jsonString: String) {
        try {
            // Validate JSON
            JSONObject(jsonString)
            _layoutJson.value = jsonString
            _error.value = null
        } catch (e: Exception) {
            Log.e(TAG, "Invalid JSON provided", e)
            _error.value = "Invalid JSON: ${e.message}"
        }
    }
    
    fun cleanup() {
        // Remove listener if needed
        // Note: We don't stop HotLoader here as it might be used by other views
        Log.d(TAG, "Cleaning up DynamicView for: $layoutName")
    }
}

/**
 * Composable function to render a DynamicView
 */
@Composable
fun DynamicView(
    layoutName: String,
    modifier: Modifier = Modifier,
    onError: ((String) -> Unit)? = null,
    onLoading: @Composable () -> Unit = {},
    content: @Composable (String) -> Unit = {}
) {
    val context = LocalContext.current
    val dynamicView = remember(layoutName) {
        DynamicView(layoutName, context)
    }
    
    val layoutJson by dynamicView.layoutJson.collectAsState()
    val isLoading by dynamicView.isLoading.collectAsState()
    val error by dynamicView.error.collectAsState()
    
    // Handle error callback
    LaunchedEffect(error) {
        error?.let { onError?.invoke(it) }
    }
    
    // Cleanup when removed from composition
    LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
            // Cleanup will be called when the coroutine is cancelled
        }
    }
    
    when {
        isLoading -> {
            onLoading()
        }
        error != null -> {
            // Error state - could show default error UI
            Log.e("DynamicView", "Error: $error")
        }
        layoutJson != null -> {
            content(layoutJson!!)
        }
        else -> {
            // No layout loaded yet
            Log.d("DynamicView", "No layout loaded for: $layoutName")
        }
    }
}