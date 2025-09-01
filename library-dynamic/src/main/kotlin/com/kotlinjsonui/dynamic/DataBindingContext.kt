package com.kotlinjsonui.dynamic

import androidx.compose.runtime.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * DataBindingContext manages the data state for dynamic UI components
 * 
 * Features:
 * - Two-way data binding support
 * - Reactive state management
 * - Expression evaluation
 * - Default value handling
 */
class DataBindingContext {
    private val _data = MutableStateFlow<Map<String, Any>>(emptyMap())
    val data: StateFlow<Map<String, Any>> = _data.asStateFlow()
    
    /**
     * Updates a single value in the data context
     */
    fun updateValue(key: String, value: Any) {
        _data.value = _data.value.toMutableMap().apply {
            put(key, value)
        }
    }
    
    /**
     * Updates multiple values in the data context
     */
    fun updateValues(updates: Map<String, Any>) {
        _data.value = _data.value.toMutableMap().apply {
            putAll(updates)
        }
    }
    
    /**
     * Gets a value from the data context
     */
    fun getValue(key: String): Any? {
        return _data.value[key]
    }
    
    /**
     * Clears all data from the context
     */
    fun clear() {
        _data.value = emptyMap()
    }
    
    /**
     * Sets the entire data map
     */
    fun setData(data: Map<String, Any>) {
        _data.value = data
    }
    
    /**
     * Evaluates a binding expression like "@{variable}" or "@{variable ?? defaultValue}"
     * 
     * @param expression The binding expression
     * @param data The data map
     * @return The evaluated value or null
     */
    companion object {
        fun evaluateExpression(expression: String, data: Map<String, Any>): Any? {
            if (!expression.startsWith("@{") || !expression.endsWith("}")) {
                return expression
            }
            
            val innerExpression = expression.substring(2, expression.length - 1)
            
            // Handle default value syntax: variable ?? defaultValue
            return if (innerExpression.contains("??")) {
                val parts = innerExpression.split("??")
                val variable = parts[0].trim()
                val defaultValue = parts[1].trim()
                
                // Try to get the value from nested path
                val value = getNestedValue(variable, data)
                value ?: parseDefaultValue(defaultValue)
            } else {
                // Simple variable lookup
                getNestedValue(innerExpression.trim(), data)
            }
        }
        
        /**
         * Gets a value from a nested path like "user.name" or "items[0].title"
         */
        private fun getNestedValue(path: String, data: Map<String, Any>): Any? {
            val parts = path.split(".")
            var current: Any? = data
            
            for (part in parts) {
                when {
                    // Handle array access like items[0]
                    part.contains("[") && part.contains("]") -> {
                        val arrayName = part.substring(0, part.indexOf("["))
                        val index = part.substring(part.indexOf("[") + 1, part.indexOf("]")).toIntOrNull()
                        
                        if (current is Map<*, *> && index != null) {
                            val array = current[arrayName]
                            current = when (array) {
                                is List<*> -> array.getOrNull(index)
                                is Array<*> -> array.getOrNull(index)
                                else -> null
                            }
                        } else {
                            return null
                        }
                    }
                    // Handle map access
                    current is Map<*, *> -> {
                        current = current[part]
                    }
                    else -> {
                        return null
                    }
                }
            }
            
            return current
        }
        
        /**
         * Parses a default value from a string
         */
        private fun parseDefaultValue(value: String): Any {
            return when {
                value == "true" -> true
                value == "false" -> false
                value == "null" -> ""
                value.startsWith("\"") && value.endsWith("\"") -> {
                    value.substring(1, value.length - 1)
                }
                value.toIntOrNull() != null -> value.toInt()
                value.toDoubleOrNull() != null -> value.toDouble()
                else -> value
            }
        }
        
        /**
         * Replaces all binding expressions in a string with their values
         */
        fun processBindings(text: String, data: Map<String, Any>): String {
            val pattern = "@\\{([^}]+)\\}".toRegex()
            return pattern.replace(text) { matchResult ->
                val expression = matchResult.value
                evaluateExpression(expression, data)?.toString() ?: ""
            }
        }
    }
}

/**
 * Composable that provides a DataBindingContext
 */
@Composable
fun rememberDataBindingContext(
    initialData: Map<String, Any> = emptyMap()
): DataBindingContext {
    return remember {
        DataBindingContext().apply {
            setData(initialData)
        }
    }
}

/**
 * Composable that provides the current data state from a DataBindingContext
 */
@Composable
fun DataBindingContext.collectAsState(): State<Map<String, Any>> {
    return data.collectAsState()
}