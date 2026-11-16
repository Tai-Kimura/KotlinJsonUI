package com.kotlinjsonui.dynamic

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Manages focus state for TextFields (focus chain support).
 * Singleton that broadcasts focus requests to all registered FocusableTextFields.
 *
 * Matching SwiftJsonUI's FocusManager:
 * - requestFocus(fieldId) → moves focus to specific field
 * - clearFocus() → clears focus from all fields
 */
object FocusManager {
    private val _focusRequestFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val focusRequestFlow = _focusRequestFlow.asSharedFlow()

    /**
     * Request focus on a specific field by its id
     */
    fun requestFocus(fieldId: String) {
        _focusRequestFlow.tryEmit(fieldId)
    }

    /**
     * Clear focus from all fields
     */
    fun clearFocus() {
        _focusRequestFlow.tryEmit("")
    }
}
