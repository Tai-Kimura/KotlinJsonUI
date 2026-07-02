package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.DynamicLayoutLoader
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.TypedAttrs
import com.kotlinjsonui.dynamic.UnappliedAttributes
import com.kotlinjsonui.dynamic.generated.EmbedAttributes
import com.kotlinjsonui.dynamic.rememberTypedAttrs
import com.kotlinjsonui.embed.EmbedContainer
import com.kotlinjsonui.embed.EmbedNavigationMode
import com.kotlinjsonui.embed.EmbeddedEvent

/**
 * Dynamic Embed Component Converter
 *
 * Renders a JSON `Embed` node at runtime. Two-tier resolution mirroring
 * [DynamicTabViewComponent]:
 *   1. Try [Configuration.customComponentHandler] for the screen name
 *      (compiled screens register a handler that instantiates their
 *      generated composable, which owns its own VM via `viewModel()`).
 *   2. Fall back to [DynamicView] loading the embedded layout JSON. The
 *      parent's `data` is intentionally NOT propagated — only the resolved
 *      `params` are passed, so the embedded layout's data section defaults
 *      take effect.
 *
 * Attribute access goes through the generated [EmbedAttributes] extraction
 * (typed, alias-aware, L1-marker-aware) via the [TypedAttrs] bridge.
 * Exception: `params` is read raw (see [TypedAttrs.rawKey] call) because
 * the legacy resolver depends on gson JsonPrimitive number identity —
 * `asNumber` keeps the authored "5" vs "5.0" spelling when the value is
 * handed to the embedded screen's data map, which the typed Double-based
 * map would collapse.
 *
 * Supported JSON attributes (matching attribute_definitions.json):
 * - `screen` (required): Name of the screen to embed.
 * - `params` (optional): Map of init params; `@{...}` values resolved
 *   against the parent data.
 * - `navigationMode` (optional): `"delegate"` (default) or `"isolated"`.
 * - `events` (optional): wired in P2.
 *
 * See jsonui-cli/docs/plans/2026-05-11-embed-feature.md.
 */
class DynamicEmbedComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            data: Map<String, Any> = emptyMap()
        ) {
            val context = LocalContext.current
            val a = rememberTypedAttrs(json) { m, canonicalOnly ->
                EmbedAttributes.parse(m, canonicalOnly)
            }
            UnappliedAttributes.check(
                "Embed", json,
                declared = EmbedAttributes.declaredAttributes,
                applied = UnappliedAttributes.COMMON_APPLIED + APPLIED,
                context = context
            )

            val screenName = a.screen.orEmpty()
            if (screenName.isEmpty()) {
                ErrorBox("Embed: missing required `screen` attribute")
                return
            }

            val embedId = a.common.id ?: "embed"
            // "isolated" is not a declared enum value (v1 declares only
            // "delegate") — it reaches us through the AttrEnum unknown
            // pass-through, matching the legacy raw-string comparison.
            val navigationMode = when (TypedAttrs.enumString(a.navigationMode) { it.json }) {
                "isolated" -> EmbedNavigationMode.Isolated
                else -> EmbedNavigationMode.Delegate
            }
            // Declared key read raw: legacy number handling relies on gson
            // JsonElement types (asNumber) — see class KDoc.
            val resolvedParams = resolveParams(TypedAttrs.rawKey(json, "params"), data)
            val eventBridge = buildEventBridge(a.events, data)

            EmbedContainer(
                embedId = embedId,
                params = resolvedParams,
                navigationMode = navigationMode,
                eventBridge = eventBridge
            ) { _ ->
                // Tier 1: compiled screen via custom component handler
                val viewJson = JsonObject().apply {
                    addProperty("type", screenName)
                }
                val handled = Configuration.customComponentHandler?.invoke(
                    screenName, viewJson, resolvedParams
                ) ?: false

                if (!handled) {
                    // Tier 2: dynamic fallback — load embedded layout JSON
                    // with only the resolved params (parent's data is NOT propagated).
                    val layoutJson = DynamicLayoutLoader.loadLayout(screenName)
                    if (layoutJson != null) {
                        DynamicView(layoutJson, resolvedParams)
                    } else {
                        ErrorBox("Embed: layout not found for screen `$screenName`")
                    }
                }
            }
        }

        /** Embed-specific attributes this component applies (see UnappliedAttributes). */
        private val APPLIED: Set<String> = setOf(
            "screen", "params", "navigationMode", "events"
        )

        /**
         * Build an event bridge from the typed `events: { onEventName:
         * "parentHandlerName" }` map. Each emitted Named event looks up the
         * handler in the parent data dict (parent VM exposes handlers as
         * functions keyed by name). Non-string values are skipped, matching
         * the legacy primitive-string filter.
         */
        private fun buildEventBridge(
            events: Map<String, Any?>?,
            parentData: Map<String, Any>
        ): ((EmbeddedEvent) -> Unit)? {
            if (events == null || events.isEmpty()) return null
            val eventMap = mutableMapOf<String, String>()
            for ((key, value) in events) {
                (value as? String)?.let { eventMap[key] = it }
            }
            if (eventMap.isEmpty()) return null
            return { event ->
                if (event is EmbeddedEvent.Named) {
                    val handlerName = eventMap[event.name]
                    if (handlerName != null) {
                        @Suppress("UNCHECKED_CAST")
                        when (val handler = parentData[handlerName]) {
                            is Function1<*, *> -> (handler as (Map<String, Any>) -> Unit).invoke(event.payload)
                            is Function0<*> -> (handler as () -> Unit).invoke()
                            else -> {} // unresolved — silently ignore
                        }
                    }
                }
            }
        }

        /**
         * Resolve `params` element: keys whose values are `@{binding}` strings
         * are looked up in the parent data dict. Literals pass through.
         */
        private fun resolveParams(
            element: com.google.gson.JsonElement?,
            parentData: Map<String, Any>
        ): Map<String, Any> {
            if (element == null || !element.isJsonObject) return emptyMap()
            val obj = element.asJsonObject
            val out = mutableMapOf<String, Any>()
            for ((key, value) in obj.entrySet()) {
                if (value.isJsonPrimitive && value.asJsonPrimitive.isString) {
                    val s = value.asString
                    if (s.startsWith("@{") && s.endsWith("}")) {
                        val prop = s.substring(2, s.length - 1)
                        parentData[prop]?.let { out[key] = it }
                        // unresolved binding → key dropped (let embedded layout's defaultValue apply)
                        continue
                    }
                    out[key] = s
                } else if (value.isJsonPrimitive) {
                    val p = value.asJsonPrimitive
                    out[key] = when {
                        p.isBoolean -> p.asBoolean
                        p.isNumber -> p.asNumber
                        else -> p.asString
                    }
                }
                // nested objects/arrays: not supported in v1
            }
            return out
        }

        @Composable
        private fun ErrorBox(message: String) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
