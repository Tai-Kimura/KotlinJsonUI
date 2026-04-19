package com.kotlinjsonui.dynamic.hotloader

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

/**
 * HotLoader client for real-time UI updates during development
 * Only active in DEBUG builds
 */
class HotLoader private constructor(context: Context) {
    // Use WeakReference to avoid memory leaks
    private val contextRef = WeakReference(context)
    
    companion object {
        private const val TAG = "KjuiHotLoader"
        private const val RECONNECT_DELAY = 5000L // 5 seconds
        private const val CONFIG_FILE = "hotloader.json"
        
        @Volatile
        private var instance: HotLoader? = null
        
        fun getInstance(context: Context): HotLoader {
            return instance ?: synchronized(this) {
                instance ?: HotLoader(context.applicationContext).also {
                    instance = it
                }
            }
        }
        
        /**
         * Clear the singleton instance to allow garbage collection
         */
        fun clearInstance() {
            instance?.stop()
            instance = null
        }
    }
    
    // Configuration
    data class Config(
        val ip: String = "10.0.2.2", // Android emulator localhost
        val port: Int = 8081,
        val enabled: Boolean = false
    ) {
        val websocketUrl: String get() = "ws://$ip:$port"
        val httpUrl: String get() = "http://$ip:$port"
    }
    
    // State
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected
    
    private val _lastUpdate = MutableStateFlow<LayoutUpdate?>(null)
    val lastUpdate: StateFlow<LayoutUpdate?> = _lastUpdate
    
    // Networking
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()
    
    private var webSocket: WebSocket? = null
    private var config: Config = loadConfig()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Callbacks
    private val listeners = mutableListOf<HotLoaderListener>()
    
    init {
        if (config.enabled) {
            connect()
        }
    }
    
    fun start() {
        config = loadConfig()
        if (config.enabled && webSocket == null) {
            connect()
        }
    }
    
    fun stop() {
        disconnect()
    }
    
    fun addListener(listener: HotLoaderListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: HotLoaderListener) {
        listeners.remove(listener)
    }
    
    private fun loadConfig(): Config {
        return try {
            val ctx = contextRef.get() ?: return Config()
            // Try to load from assets
            val configJson = ctx.assets.open(CONFIG_FILE).bufferedReader().use { it.readText() }
            val json = JSONObject(configJson)
            
            Config(
                ip = json.optString("ip", "10.0.2.2"),
                port = json.optInt("port", 8081),
                enabled = json.optBoolean("enabled", false)
            )
        } catch (e: Exception) {
            Log.w(TAG, "Could not load config from assets, using defaults", e)
            
            // Try to load from local.properties
            try {
                val ctx = contextRef.get() ?: return Config()
                val localProps = File(ctx.filesDir.parentFile?.parentFile, "local.properties")
                if (localProps.exists()) {
                    val props = localProps.readLines()
                    val ip = props.find { it.startsWith("hotloader.ip=") }?.substringAfter("=")
                    val port = props.find { it.startsWith("hotloader.port=") }?.substringAfter("=")?.toIntOrNull()
                    
                    if (ip != null) {
                        return Config(
                            ip = ip,
                            port = port ?: 8081,
                            enabled = true
                        )
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Could not load config from local.properties", e)
            }
            
            // Default config
            Config()
        }
    }
    
    private fun connect() {
        Log.d(TAG, "Connecting to ${config.websocketUrl}")
        
        val request = Request.Builder()
            .url(config.websocketUrl)
            .build()
        
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.i(TAG, "Connected to HotLoader server")
                _isConnected.value = true
                
                listeners.forEach { it.onConnected() }
            }
            
            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "Received message: $text")
                handleMessage(text)
            }
            
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.i(TAG, "WebSocket closing: $reason")
                webSocket.close(1000, null)
            }
            
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.i(TAG, "WebSocket closed: $reason")
                _isConnected.value = false
                this@HotLoader.webSocket = null
                
                listeners.forEach { it.onDisconnected() }
                
                // Attempt reconnection after delay
                scope.launch {
                    delay(RECONNECT_DELAY)
                    if (config.enabled) {
                        connect()
                    }
                }
            }
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e(TAG, "WebSocket failure", t)
                _isConnected.value = false
                this@HotLoader.webSocket = null
                
                listeners.forEach { it.onError(t) }
                
                // Attempt reconnection after delay
                scope.launch {
                    delay(RECONNECT_DELAY)
                    if (config.enabled) {
                        connect()
                    }
                }
            }
        })
    }
    
    private fun disconnect() {
        webSocket?.close(1000, "User requested disconnect")
        webSocket = null
        _isConnected.value = false
    }
    
    private fun handleMessage(message: String) {
        try {
            val json = JSONObject(message)
            val type = json.optString("type")

            when (type) {
                "connected" -> {
                    Log.i(TAG, "Server confirmed connection")
                }
                "file_changed" -> {
                    val path = json.optString("path")
                    val fileName = json.optString("fileName")

                    Log.i(TAG, "File changed: $path")

                    // Extract layout path from full path (e.g., "app/src/main/assets/Layouts/home/home_header.json")
                    val layoutPath = extractLayoutPath(path)
                    val stylePath = extractStylePath(path)

                    when {
                        layoutPath != null -> {
                            // Download the updated layout with subdirectory path
                            downloadLayout(layoutPath)
                        }
                        stylePath != null -> {
                            // Download the updated style
                            downloadStyle(stylePath)
                        }
                    }
                }
                "file_added" -> {
                    val path = json.optString("path")
                    val layoutPath = extractLayoutPath(path)

                    Log.i(TAG, "File added: $path")
                    if (layoutPath != null) {
                        listeners.forEach { it.onLayoutAdded(layoutPath) }
                    }
                }
                "file_removed" -> {
                    val path = json.optString("path")
                    val layoutPath = extractLayoutPath(path)

                    Log.i(TAG, "File removed: $path")
                    if (layoutPath != null) {
                        listeners.forEach { it.onLayoutRemoved(layoutPath) }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling message", e)
        }
    }

    /**
     * Extract layout path from full file path
     * e.g., "app/src/main/assets/Layouts/home/home_header.json" -> "home/home_header"
     */
    private fun extractLayoutPath(path: String): String? {
        val layoutsIndex = path.indexOf("Layouts/")
        if (layoutsIndex == -1) return null

        val relativePath = path.substring(layoutsIndex + "Layouts/".length)
        return relativePath.removeSuffix(".json")
    }

    /**
     * Extract style path from full file path
     * e.g., "app/src/main/assets/Styles/common.json" -> "common"
     */
    private fun extractStylePath(path: String): String? {
        val stylesIndex = path.indexOf("Styles/")
        if (stylesIndex == -1) return null

        val relativePath = path.substring(stylesIndex + "Styles/".length)
        return relativePath.removeSuffix(".json")
    }
    
    private fun downloadLayout(layoutName: String) {
        scope.launch {
            try {
                val url = "${config.httpUrl}/layout/$layoutName"
                val request = Request.Builder()
                    .url(url)
                    .build()
                
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val layoutJson = response.body?.string()
                    if (layoutJson != null) {
                        val update = LayoutUpdate(layoutName, layoutJson, System.currentTimeMillis())
                        _lastUpdate.value = update
                        
                        // Save to cache
                        saveLayoutToCache(layoutName, layoutJson)
                        
                        // Notify listeners
                        withContext(Dispatchers.Main) {
                            listeners.forEach { it.onLayoutUpdated(layoutName, layoutJson) }
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to download layout: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading layout", e)
            }
        }
    }
    
    private fun downloadStyle(styleName: String) {
        scope.launch {
            try {
                val url = "${config.httpUrl}/style/$styleName"
                val request = Request.Builder()
                    .url(url)
                    .build()
                
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val styleJson = response.body?.string()
                    if (styleJson != null) {
                        // Save to cache
                        saveStyleToCache(styleName, styleJson)
                        
                        // Clear the style cache in DynamicStyleLoader
                        withContext(Dispatchers.Main) {
                            com.kotlinjsonui.dynamic.DynamicStyleLoader.clearCache()
                            
                            // Notify listeners about style update
                            listeners.forEach { it.onStyleUpdated(styleName, styleJson) }
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to download style: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading style", e)
            }
        }
    }
    
    private fun saveLayoutToCache(layoutName: String, content: String) {
        try {
            val ctx = contextRef.get() ?: return
            val cacheDir = File(ctx.cacheDir, "hotloader_layouts")

            // Handle subdirectory paths (e.g., "home/home_header")
            val file = File(cacheDir, "$layoutName.json")
            file.parentFile?.mkdirs()

            file.writeText(content)

            Log.d(TAG, "Saved layout to cache: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving layout to cache", e)
        }
    }
    
    private fun saveStyleToCache(styleName: String, content: String) {
        try {
            val ctx = contextRef.get() ?: return
            val cacheDir = File(ctx.cacheDir, "hotloader_styles")
            cacheDir.mkdirs()
            
            val file = File(cacheDir, "$styleName.json")
            file.writeText(content)
            
            Log.d(TAG, "Saved style to cache: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving style to cache", e)
        }
    }
    
    fun getCachedLayout(layoutName: String): String? {
        return try {
            val ctx = contextRef.get() ?: return null
            val cacheDir = File(ctx.cacheDir, "hotloader_layouts")
            val file = File(cacheDir, "$layoutName.json")
            
            if (file.exists()) {
                file.readText()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading cached layout", e)
            null
        }
    }
    
    fun clearCache() {
        try {
            val ctx = contextRef.get() ?: return
            val cacheDir = File(ctx.cacheDir, "hotloader_layouts")
            cacheDir.deleteRecursively()
            Log.i(TAG, "Cache cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cache", e)
        }
    }
    
    // Data classes
    data class LayoutUpdate(
        val layoutName: String,
        val content: String,
        val timestamp: Long
    )
    
    // Listener interface
    interface HotLoaderListener {
        fun onConnected()
        fun onDisconnected()
        fun onLayoutUpdated(layoutName: String, content: String)
        fun onLayoutAdded(layoutName: String)
        fun onLayoutRemoved(layoutName: String)
        fun onStyleUpdated(styleName: String, content: String) {}
        fun onError(error: Throwable)
    }
}