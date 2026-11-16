// Pasted from what `kjui build` emits for DisabledColoursProbeTest's layout — kjui_tools of jsonui-cli
// fix/bound-enabled-disabled-colors 27adb275 (ticket 2026-10-23-bound-enabled-disabled-colors-three-faces): a bound
// `enabled` chooses the disabled colours. Changed from the output: the package ({com.example.app.*,
// com.kotlinjsonui.generated} → com.kotlinjsonui.conformance.disabledcolours, so the generated ColorManager is the probe's own and never the codegen
// host's), the imports that named them, and the project's resources written as their values
// (colorResource(R.color.dark_blue) → Color(0xFF0000FF), R.color.light_gray → Color(0xFF999999),
// stringResource(R.string.disabled_colours_mmmm) → "MMMM"). The `if (data.on) … else …` choices are as emitted.
// ╔══════════════════════════════════════════════════════════════════╗
// ║  @generated AUTO-GENERATED FILE — DO NOT EDIT
// ║  Source:    ColorManager (colors from colors.json)
// ║  Generator: kjui build
// ║  Any manual edits will be OVERWRITTEN on next generation.
// ║  LLM/Agent: you MUST NOT modify this file.
// ╚══════════════════════════════════════════════════════════════════╝

package com.kotlinjsonui.conformance.disabledcolours

import android.graphics.Color
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color as ComposeColor

object ColorManager {
    private const val TAG = "ColorManager"

    /** Color modes discovered in colors.json. Add a new top-level mode object there to grow this list. */
    enum class ColorMode(val raw: String) {
        LIGHT("light");

        companion object {
            fun fromRaw(raw: String): ColorMode? = values().firstOrNull { it.raw == raw }
        }
    }

    val fallbackMode: ColorMode = ColorMode.LIGHT

    /** Map from OS appearance (light/dark) to project-specific mode. */
    val systemModeMapping: Map<String, ColorMode> = mapOf(
        "light" to ColorMode.LIGHT
    )

    /** Raw hex palette per mode (snake_case keys). */
    private val palettes: Map<ColorMode, Map<String, String>> = mapOf(
        ColorMode.LIGHT to mapOf(
            "dark_blue" to "#0000FF",
            "light_gray" to "#999999"
        )
    )

    /** Reactive current-mode holder. Reading `value` from @Composable code triggers recomposition. */
    private val _currentMode: MutableState<ColorMode> = mutableStateOf(fallbackMode)
    val currentMode: ColorMode get() = _currentMode.value

    private val observers: MutableMap<Any, () -> Unit> = mutableMapOf()
    private var followSystemMode: Boolean = true

    fun setMode(mode: ColorMode) {
        if (_currentMode.value == mode) return
        _currentMode.value = mode
        observers.values.forEach { it.invoke() }
    }

    fun setFollowSystemMode(follow: Boolean) { followSystemMode = follow }
    fun isFollowingSystemMode(): Boolean = followSystemMode

    /** Subscribe with any Any key; returns a closure that unsubscribes. */
    fun subscribe(key: Any, callback: () -> Unit): () -> Unit {
        observers[key] = callback
        return { observers.remove(key) }
    }

    /** Call from Activity/Application when night mode changes, using Configuration.uiMode. */
    fun applySystemMode(osMode: String) {
        if (!followSystemMode) return
        systemModeMapping[osMode]?.let { setMode(it) }
    }

    // ========== Android Views ==========
    object views {
        fun color(key: String): Int? {
            if (key.startsWith("@{") && key.endsWith("}")) return null
            val mode = currentMode
            val hex = palettes[mode]?.get(key) ?: palettes[fallbackMode]?.get(key)
            if (hex != null) {
                return try { Color.parseColor(hex) }
                catch (e: IllegalArgumentException) { Log.w(TAG, "Invalid hex '$hex' for key '$key'"); null }
            }
            return try { Color.parseColor(key) } catch (e: IllegalArgumentException) { null }
        }

        val darkBlue: Int? get() = color("dark_blue")
        val lightGray: Int? get() = color("light_gray")

        /** Fixed values from `light` palette (not affected by setMode). */
        object light {
            val darkBlue: Int? get() = try { Color.parseColor("#0000FF") } catch (e: IllegalArgumentException) { null }
            val lightGray: Int? get() = try { Color.parseColor("#999999") } catch (e: IllegalArgumentException) { null }
        }
    }

    // ========== Jetpack Compose ==========
    object compose {
        /** Reading this from @Composable code triggers recomposition on mode change. */
        fun color(key: String): ComposeColor? {
            val int = views.color(key) ?: return null
            return ComposeColor(int)
        }

        /**
         * Runtime coercion for Color-typed data fields: a palette token
         * first, then a hex literal. The data contract declares Color
         * fields with token-string defaults, so a runtime String IS a
         * legal value — `as? Color` alone silently dropped it while the
         * dynamic path resolved it (a downstream detail screen, 2026-08-08).
         */
        fun colorOrHex(value: String): ComposeColor? =
            color(value) ?: try { ComposeColor(Color.parseColor(value)) }
            catch (e: IllegalArgumentException) { null }

        val darkBlue: ComposeColor? get() = color("dark_blue")
        val lightGray: ComposeColor? get() = color("light_gray")

        object light {
            val darkBlue: ComposeColor? get() = views.light.darkBlue?.let { ComposeColor(it) }
            val lightGray: ComposeColor? get() = views.light.lightGray?.let { ComposeColor(it) }
        }
    }
}

// ══ END AUTO-GENERATED — DO NOT APPEND BELOW THIS LINE ══