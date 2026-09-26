package com.kotlinjsonui.dynamic

import android.util.Log
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStreamReader

/**
 * The type-spelling synonyms — a node `type` that is not a declared section
 * (HStack, ProgressBar, WebView, …) and the type it is drawn as.
 *
 * The table is jsonui-cli's `shared/core/type_synonyms.json`, vendored byte
 * for byte as a Java resource of this library ([RESOURCE]); CI compares the
 * copy with the pinned jsonui-cli ref. Validation (jui_cli, the Ruby tools)
 * and the codegen converters read the same file, so no renderer holds a
 * list of its own.
 *
 * A synonym draws as its `canonical` type, or as `render_as` where the
 * entry names one (CircleImage keeps its circular converter). Any other key
 * of an entry is an attribute the spelling means — HStack is a View with
 * `orientation: horizontal` — added where the node does not set it.
 *
 * A type the app registers as its own component is asked for before this
 * (DynamicView asks Configuration.customComponentHandler with the node as
 * written) and is never rewritten.
 *
 * The file ships inside the library. When it cannot be read, the first
 * lookup throws, naming the resource: drawing every synonym spelling as an
 * unknown type, quietly, is the failure this refuses.
 */
object TypeSynonyms {
    /** Where the vendored copy sits among this library's Java resources. */
    const val RESOURCE: String = "/com/kotlinjsonui/dynamic/type_synonyms.json"

    private const val TAG = "JsonUITypeSynonyms"

    /** One entry: the section that declares the spelling, what it is drawn as, and what it means. */
    data class Entry(
        val canonical: String,
        val renderAs: String?,
        /** Attributes the spelling means (HStack: orientation horizontal). */
        val implied: Map<String, JsonElement>,
    ) {
        val drawnAs: String get() = renderAs ?: canonical
    }

    /**
     * Spelling → entry, as written: type names are case-sensitive (4f's
     * ruling, jsonui-cli 1.9.0; the codegens match them so). It was keyed
     * lowercase, as DynamicView's dispatch then was.
     */
    val entries: Map<String, Entry> by lazy { load() }

    /** Test hook: receives every warning message. */
    var warningSink: ((String) -> Unit)? = null

    private val warned = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    /** The type [type] is drawn as: its synonym's target, or itself. */
    fun drawnAs(type: String): String = entries[type]?.drawnAs ?: type

    /**
     * [node] as drawn. For a synonym, a copy whose `type` is what it is drawn
     * as, with the attributes its spelling means added where the node does
     * not set them. Where the node sets one otherwise (an HStack with
     * `orientation: vertical`), the node's value stays and a warning names
     * both, once per spelling and value. Anything else is [node] itself.
     */
    fun canonicalize(node: JsonObject, context: android.content.Context? = null): JsonObject {
        val type = node.get("type")?.takeIf { it.isJsonPrimitive }?.asString ?: return node
        val entry = entries[type] ?: return node
        val drawn = JsonObject()
        for ((key, value) in node.entrySet()) drawn.add(key, value)
        drawn.addProperty("type", entry.drawnAs)
        for ((key, meant) in entry.implied) {
            val given = node.get(key)
            if (given == null) {
                drawn.add(key, meant)
            } else if (given != meant && warned.add("$type:$key:$given")) {
                val message = "$type means $key $meant, and the node sets $key $given: " +
                    "drawn as ${entry.drawnAs} with $key $given"
                warningSink?.invoke(message)
                if (DebugDiagnostics.isAppDebuggable(context)) Log.w(TAG, message)
            }
        }
        return drawn
    }

    private fun load(): Map<String, Entry> = parse(TypeSynonyms::class.java.getResourceAsStream(RESOURCE))

    /** The entries in [stream], the vendored file; null — the resource is missing — throws. */
    internal fun parse(stream: java.io.InputStream?): Map<String, Entry> {
        stream
            ?: throw IllegalStateException(
                "KotlinJsonUI: $RESOURCE is not on the classpath. The type-synonym table ships " +
                    "inside library-dynamic; without it no synonym spelling (HStack, ProgressBar, " +
                    "WebView, …) can be drawn as its type."
            )
        val root = stream.use { JsonParser.parseReader(InputStreamReader(it, Charsets.UTF_8)) }
        val synonyms = root.takeIf { it.isJsonObject }?.asJsonObject?.get("synonyms")
            ?.takeIf { it.isJsonObject }?.asJsonObject
            ?: throw IllegalStateException("KotlinJsonUI: $RESOURCE has no `synonyms` object")
        return synonyms.entrySet().associate { (spelling, value) ->
            val entry = value.takeIf { it.isJsonObject }?.asJsonObject
            val canonical = entry?.get("canonical")?.takeIf { it.isJsonPrimitive }?.asString
                ?: throw IllegalStateException("KotlinJsonUI: $RESOURCE entry `$spelling` has no `canonical`")
            val renderAs = entry.get("render_as")?.takeIf { it.isJsonPrimitive }?.asString
            val implied = entry.entrySet()
                .filter { (key, _) -> key != "canonical" && key != "render_as" }
                .associate { (key, v) -> key to v }
            spelling to Entry(canonical, renderAs, implied)
        }
    }
}
