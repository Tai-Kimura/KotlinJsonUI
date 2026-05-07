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
 * HotLoader client for the jui-tools centralized hotload server.
 *
 * Wire protocol v2 (KotlinJsonUI 2.4.0):
 *   client → server: { "type": "hello", "platform": "android" }
 *   server → client: { "type": "welcome", ... }
 *   server → client: { "type": "hello_ack", "platform": "android" }
 *   server → client: { "type": "layout_changed", "layout": "home/home_header", ... }
 *   server → client: { "type": "style_changed", "style": "card", ... }
 *
 * Config loaded from assets/hotloader.json (schema: server.{host,port,wsPath},
 * client.{ip,fallbackToLocalhost}). See `jui hotload listen`.
 */
class HotLoader private constructor(context: Context) {
    private val contextRef = WeakReference(context)

    companion object {
        private const val TAG = "KjuiHotLoader"
        private const val RECONNECT_DELAY = 5000L
        private const val CONFIG_FILE = "hotloader.json"
        private const val PLATFORM = "android"

        @Volatile
        private var instance: HotLoader? = null

        fun getInstance(context: Context): HotLoader {
            return instance ?: synchronized(this) {
                instance ?: HotLoader(context.applicationContext).also {
                    instance = it
                }
            }
        }

        fun clearInstance() {
            instance?.stop()
            instance = null
        }
    }

    data class Config(
        val ip: String = "10.0.2.2",
        val port: Int = 8081,
        val wsPath: String = "/ws",
        val enabled: Boolean = true
    ) {
        val websocketUrl: String get() = "ws://$ip:$port$wsPath"
        val httpUrl: String get() = "http://$ip:$port"
    }

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _lastUpdate = MutableStateFlow<LayoutUpdate?>(null)
    val lastUpdate: StateFlow<LayoutUpdate?> = _lastUpdate

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private var config: Config = loadConfig()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
        val ctx = contextRef.get() ?: return Config()
        return try {
            val configJson = ctx.assets.open(CONFIG_FILE).bufferedReader().use { it.readText() }
            parseConfig(configJson)
        } catch (e: Exception) {
            Log.w(TAG, "Could not load $CONFIG_FILE from assets, using defaults", e)
            Config()
        }
    }

    private fun parseConfig(configJson: String): Config {
        val json = JSONObject(configJson)
        val server = json.optJSONObject("server") ?: JSONObject()
        val clientSection = json.optJSONObject("client") ?: JSONObject()

        val port = server.optInt("port", 8081)
        val wsPath = server.optString("wsPath", "/ws")

        val configuredIp = clientSection.optString("ip", "")
        val fallbackToLocalhost = clientSection.optBoolean("fallbackToLocalhost", true)

        val ip = when {
            configuredIp.isNotBlank() -> configuredIp
            fallbackToLocalhost -> "10.0.2.2" // Android emulator loopback
            else -> server.optString("host", "10.0.2.2").ifBlank { "10.0.2.2" }
        }

        return Config(
            ip = ip,
            port = port,
            wsPath = wsPath,
            enabled = true
        )
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
                sendHello(webSocket)
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

                scope.launch {
                    delay(RECONNECT_DELAY)
                    if (config.enabled) {
                        connect()
                    }
                }
            }
        })
    }

    private fun sendHello(socket: WebSocket) {
        val hello = JSONObject()
        hello.put("type", "hello")
        hello.put("platform", PLATFORM)
        socket.send(hello.toString())
    }

    private fun disconnect() {
        webSocket?.close(1000, "User requested disconnect")
        webSocket = null
        _isConnected.value = false
    }

    private fun handleMessage(message: String) {
        try {
            val json = JSONObject(message)
            when (json.optString("type")) {
                "welcome" -> Log.i(TAG, "Server welcome: v${json.optString("serverVersion")}")
                "hello_ack" -> Log.i(TAG, "Server ack platform ${json.optString("platform")}")
                "layout_changed" -> {
                    val layout = json.optString("layout")
                    if (layout.isNotBlank()) {
                        Log.i(TAG, "Layout changed: $layout")
                        downloadLayout(layout)
                    }
                }
                "style_changed" -> {
                    val styleName = json.optString("style")
                    Log.i(TAG, "Style changed: $styleName — clearing cache")
                    scope.launch(Dispatchers.Main) {
                        com.kotlinjsonui.dynamic.DynamicStyleLoader.clearCache()
                        listeners.forEach { it.onStyleUpdated(styleName, "") }
                    }
                }
                "error" -> Log.w(TAG, "Server error: ${json.optString("reason")}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling message", e)
        }
    }

    private fun downloadLayout(layoutName: String) {
        scope.launch {
            try {
                val url = "${config.httpUrl}/$PLATFORM/layout/$layoutName"
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val layoutJson = response.body?.string()
                    if (layoutJson != null) {
                        val update = LayoutUpdate(layoutName, layoutJson, System.currentTimeMillis())
                        _lastUpdate.value = update
                        saveLayoutToCache(layoutName, layoutJson)
                        withContext(Dispatchers.Main) {
                            listeners.forEach { it.onLayoutUpdated(layoutName, layoutJson) }
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to download layout $layoutName: HTTP ${response.code}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading layout $layoutName", e)
            }
        }
    }

    private fun saveLayoutToCache(layoutName: String, content: String) {
        try {
            val ctx = contextRef.get() ?: return
            val cacheDir = File(ctx.cacheDir, "hotloader_layouts")
            val file = File(cacheDir, "$layoutName.json")
            file.parentFile?.mkdirs()
            file.writeText(content)
            Log.d(TAG, "Saved layout to cache: ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving layout to cache", e)
        }
    }

    fun getCachedLayout(layoutName: String): String? {
        return try {
            val ctx = contextRef.get() ?: return null
            val cacheDir = File(ctx.cacheDir, "hotloader_layouts")
            val file = File(cacheDir, "$layoutName.json")
            if (file.exists()) file.readText() else null
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

    data class LayoutUpdate(
        val layoutName: String,
        val content: String,
        val timestamp: Long
    )

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
