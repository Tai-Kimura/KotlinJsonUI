package com.kotlinjsonui.conformance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DataBindingContext
import com.kotlinjsonui.embed.EmbedNavigatorRegistry

/**
 * conformanceState provider — the ONE generic mechanism this host implements
 * for every `class: interactive` fixture (INTERACTIVE_HOST_CONTRACT.md).
 * Mirrors the web host's `conformanceState.tsx` StateHost. No fixture-specific
 * code exists anywhere in this host.
 *
 * Contract mapping (Android / Compose dynamic mode):
 * 1. Initial values — provisioned from the fixture layout's `data` section by
 *    the production path, `DynamicView.applyDataSectionDefaults` (defaults
 *    fill every key missing from the map built here).
 * 2. Handlers — every manifest `state.handlers` entry becomes a `() -> Unit`
 *    closure registered under its name in the data map; invoking it sets one
 *    variable to a literal string via [DataBindingContext.updateValue]. Any
 *    callback payload is ignored: `ModifierBuilder.resolveEventHandler` falls
 *    through `(String, Any) -> Unit` / `(Any) -> Unit` to `() -> Unit`.
 * 3. Two-way write-back — the dynamic input components route edits through
 *    the `updateData` function in the data map (the production generated-code
 *    shape); it delegates to [DataBindingContext.updateValues].
 */
object ConformanceStateRegistry {

    /** One manifest-declared handler (INTERACTIVE_HOST_CONTRACT.md, two kinds). */
    sealed interface DeclaredHandler {
        val name: String
    }

    /** Kind 1: invoking it sets [varName] to the literal [value]. */
    data class SetHandler(
        override val name: String,
        val varName: String,
        val value: String,
    ) : DeclaredHandler

    /**
     * Kind 2: invoking it drives an isolated embed's private stack through
     * [com.kotlinjsonui.embed.EmbedNavigatorRegistry].
     */
    data class EmbedHandler(
        override val name: String,
        val embedId: String,
        val action: String,
        val screen: String?,
        val params: Map<String, Any>,
    ) : DeclaredHandler

    private class ManifestIndex(
        val handlersById: Map<String, List<DeclaredHandler>>,
        val layoutById: Map<String, String>,
    )

    @Volatile
    private var index: ManifestIndex? = null

    /** Manifest `state.handlers` declarations for [fixtureId] (empty for non-interactive). */
    fun handlersFor(context: Context, fixtureId: String): List<DeclaredHandler> =
        indexFor(context).handlersById[fixtureId] ?: emptyList()

    /**
     * Asset path of a fixture's layout, from the manifest `layout` field.
     * Cannot be derived from the fixture id: case-colliding attribute pairs
     * (onclick/onClick) get de-duplicated layout FILENAMES (`..._2.layout.json`)
     * by the generator while keeping the undecorated fixture id.
     * Falls back to the id-derived path when the manifest lacks the fixture.
     */
    fun layoutAssetPath(context: Context, fixtureId: String): String {
        val layout = indexFor(context).layoutById[fixtureId]
            ?: "fixtures/$fixtureId.layout.json"
        return "conformance/$layout"
    }

    private fun indexFor(context: Context): ManifestIndex =
        index ?: parseManifest(context).also { index = it }

    private fun parseManifest(context: Context): ManifestIndex {
        val text = context.assets.open("conformance/manifest.json")
            .bufferedReader().use { it.readText() }
        val fixtures = JsonParser.parseString(text).asJsonObject.getAsJsonArray("fixtures")
        val handlersById = mutableMapOf<String, List<DeclaredHandler>>()
        val layoutById = mutableMapOf<String, String>()
        for (element in fixtures) {
            val fixture = element.asJsonObject
            val id = fixture.get("id")?.asString ?: continue
            fixture.get("layout")?.takeIf { it.isJsonPrimitive }?.asString?.let {
                layoutById[id] = it
            }
            val state = fixture.get("state")?.takeIf { it.isJsonObject }?.asJsonObject ?: continue
            val handlers = state.get("handlers")?.takeIf { it.isJsonArray }?.asJsonArray ?: continue
            val declared = handlers.mapNotNull { h ->
                val obj = h.takeIf { it.isJsonObject }?.asJsonObject ?: return@mapNotNull null
                val name = obj.get("name")?.asString ?: return@mapNotNull null
                val set = obj.get("set")?.takeIf { it.isJsonObject }?.asJsonObject
                if (set != null) {
                    return@mapNotNull SetHandler(
                        name = name,
                        varName = set.get("var")?.asString ?: return@mapNotNull null,
                        value = set.get("value")?.asString ?: return@mapNotNull null,
                    )
                }
                val embed = obj.get("embed")?.takeIf { it.isJsonObject }?.asJsonObject
                    ?: return@mapNotNull null
                EmbedHandler(
                    name = name,
                    embedId = embed.get("id")?.asString ?: return@mapNotNull null,
                    action = embed.get("action")?.asString ?: return@mapNotNull null,
                    screen = embed.get("screen")?.takeIf { it.isJsonPrimitive }?.asString,
                    params = embed.get("params")?.takeIf { it.isJsonObject }?.asJsonObject
                        ?.entrySet()
                        ?.mapNotNull { (key, value) ->
                            value.takeIf { it.isJsonPrimitive }?.asString?.let { key to it as Any }
                        }
                        ?.toMap()
                        ?: emptyMap(),
                )
            }
            if (declared.isNotEmpty()) handlersById[id] = declared
        }
        return ManifestIndex(handlersById, layoutById)
    }
}

/**
 * Data map for one rendered fixture: current mutable state + manifest-declared
 * handler closures + the generic `updateData` two-way write-back sink.
 * State is scoped to the composition (fresh per fixture — the Activity renders
 * each fixture under `key(fixtureId)`).
 */
@Composable
fun rememberConformanceData(fixtureId: String): Map<String, Any> {
    val context = LocalContext.current
    val handlers = remember(fixtureId) {
        ConformanceStateRegistry.handlersFor(context, fixtureId)
    }
    val bindingContext = remember(fixtureId) { DataBindingContext() }
    val stateData by bindingContext.data.collectAsState()

    return remember(fixtureId, stateData) {
        buildMap {
            putAll(stateData)
            for (handler in handlers) {
                val fire: () -> Unit = when (handler) {
                    is ConformanceStateRegistry.SetHandler -> {
                        { bindingContext.updateValue(handler.varName, handler.value) }
                    }
                    is ConformanceStateRegistry.EmbedHandler -> {
                        {
                            val navigator = EmbedNavigatorRegistry.get(handler.embedId)
                            when {
                                navigator == null -> Unit
                                handler.action == "push" && handler.screen != null ->
                                    navigator.push(handler.screen, handler.params)
                                handler.action == "pop" -> navigator.pop()
                            }
                        }
                    }
                }
                put(handler.name, fire)
            }
            val updateData: (Map<String, Any>) -> Unit = bindingContext::updateValues
            put("updateData", updateData)
        }
    }
}
