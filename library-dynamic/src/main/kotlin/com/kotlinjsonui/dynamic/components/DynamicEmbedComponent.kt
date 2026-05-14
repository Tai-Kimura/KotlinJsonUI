package com.kotlinjsonui.dynamic.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonObject
import com.kotlinjsonui.core.Configuration
import com.kotlinjsonui.dynamic.DynamicLayoutLoader
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.embed.EmbedContainer
import com.kotlinjsonui.dynamic.embed.EmbedNavigationMode

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
            val screenName = json.get("screen")?.asString.orEmpty()
            if (screenName.isEmpty()) {
                ErrorBox("Embed: missing required `screen` attribute")
                return
            }

            val embedId = json.get("id")?.asString ?: "embed"
            val navigationMode = when (json.get("navigationMode")?.asString) {
                "isolated" -> EmbedNavigationMode.Isolated
                else -> EmbedNavigationMode.Delegate
            }
            val resolvedParams = resolveParams(json.get("params"), data)

            EmbedContainer(
                embedId = embedId,
                navigationMode = navigationMode
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
