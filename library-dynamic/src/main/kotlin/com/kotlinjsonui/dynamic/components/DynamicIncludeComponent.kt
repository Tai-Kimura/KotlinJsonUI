package com.kotlinjsonui.dynamic.components

import androidx.compose.runtime.Composable
import com.google.gson.JsonObject
import com.kotlinjsonui.dynamic.DynamicView
import com.kotlinjsonui.dynamic.DynamicLayoutLoader
import com.kotlinjsonui.dynamic.processDataBinding

/**
 * Dynamic Include Component
 * Handles inclusion of other layout files
 * 
 * Supported JSON attributes:
 * - include: String name of the layout file to include (without .json extension)
 * - data: Object with data to pass to the included layout (creates new variables in child scope)
 * - shared_data: Object with data to merge with parent data (passes through parent variables)
 * 
 * Data bindings:
 * - In data: "@{userName}" references parent's userName and passes it as a new variable
 * - In shared_data: "@{userName}" passes parent's userName directly through
 */
class DynamicIncludeComponent {
    companion object {
        @Composable
        fun create(
            json: JsonObject,
            parentData: Map<String, Any> = emptyMap()
        ) {
            val layoutName = json.get("include")?.asString ?: return
            
            // Build data for the included layout
            val data = buildIncludeData(json, parentData)
            
            // Load and render the included layout
            val includedJson = DynamicLayoutLoader.loadLayout(layoutName)
            if (includedJson != null) {
                DynamicView(includedJson, data)
            }
        }
        
        private fun buildIncludeData(
            json: JsonObject,
            parentData: Map<String, Any>
        ): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            
            // First, add shared_data (passes through parent data with possible bindings)
            json.get("shared_data")?.asJsonObject?.let { sharedData ->
                sharedData.entrySet().forEach { (key, value) ->
                    when {
                        value.isJsonPrimitive -> {
                            val primitive = value.asJsonPrimitive
                            when {
                                primitive.isString -> {
                                    val stringValue = primitive.asString
                                    // Check for data binding
                                    if (stringValue.contains("@{")) {
                                        // Process binding using parent data
                                        val processedValue = processDataBinding(stringValue, parentData)
                                        result[key] = processedValue
                                    } else {
                                        result[key] = stringValue
                                    }
                                }
                                primitive.isBoolean -> result[key] = primitive.asBoolean
                                primitive.isNumber -> result[key] = primitive.asNumber
                            }
                        }
                        value.isJsonObject -> result[key] = value.asJsonObject
                        value.isJsonArray -> result[key] = value.asJsonArray
                    }
                }
            }
            
            // Then add data (creates new variables in child scope, can reference parent data)
            json.get("data")?.asJsonObject?.let { includeData ->
                includeData.entrySet().forEach { (key, value) ->
                    when {
                        value.isJsonPrimitive -> {
                            val primitive = value.asJsonPrimitive
                            when {
                                primitive.isString -> {
                                    val stringValue = primitive.asString
                                    // Check for data binding
                                    if (stringValue.contains("@{")) {
                                        // Process binding using parent data
                                        val processedValue = processDataBinding(stringValue, parentData)
                                        result[key] = processedValue
                                    } else {
                                        result[key] = stringValue
                                    }
                                }
                                primitive.isBoolean -> result[key] = primitive.asBoolean
                                primitive.isNumber -> result[key] = primitive.asNumber
                            }
                        }
                        value.isJsonObject -> result[key] = value.asJsonObject
                        value.isJsonArray -> result[key] = value.asJsonArray
                    }
                }
            }
            
            // Finally, add any parent data that wasn't overridden
            // This ensures functions and other parent data are still accessible
            parentData.forEach { (key, value) ->
                if (!result.containsKey(key)) {
                    result[key] = value
                }
            }
            
            return result
        }
    }
}