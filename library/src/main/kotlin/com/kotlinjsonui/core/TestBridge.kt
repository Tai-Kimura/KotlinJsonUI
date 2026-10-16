package com.kotlinjsonui.core

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import java.math.BigDecimal

/**
 * The test bridge (jsonui-test-runner spec §10): a test's initialState reaches
 * the screen's Data, and the screen's Data is published back to the driver.
 *
 * Everything here runs only behind the screen marker's gate (a debuggable
 * build, or the `com.kotlinjsonui.SCREEN_MARKER` meta-data): a closed gate
 * means no reading of the payload and no bridge.
 */

/** Intent extra carrying the initialState payload (spec §10.2). */
const val TEST_INITIAL_STATE_EXTRA = "JSONUI_TEST_INITIAL_STATE"

/** Semantics key on the screen marker node carrying the bridge JSON (§10.4). */
val JsonUIStateKey = SemanticsPropertyKey<String>(
    name = "JsonUIState",
    mergePolicy = { parent, _ -> parent }
)

/** A key the app did not apply, and why (§10.3: fixed reason strings). */
data class RejectedKey(val key: String, val reason: String)

/** What the check answers for one value. */
sealed interface TestValueVerdict {
    /** Applied as [value], already in the Kotlin type the Data field holds. */
    data class Accepted(val value: Any?) : TestValueVerdict
    data class Rejected(val reason: String) : TestValueVerdict
}

/**
 * The one place the type of an initialState value is checked (§10.3). The
 * generated Data code calls it per field with the class the layout wrote;
 * nothing else decides what a value may be.
 *
 * Values arrive parsed from JSON ([JsonUITestValue.parseJson]): strings,
 * booleans, numbers as [BigDecimal], lists, maps with string keys, null.
 */
object JsonUITestValue {

    private enum class Base { STRING, INT, DOUBLE, FLOAT, BOOL, COLOR }

    private sealed interface Shape {
        data class Primitive(val base: Base) : Shape
        data class ListOf(val element: Base) : Shape
        data object AnyPrimitiveList : Shape
        data object PrimitiveMap : Shape
        data object Unsupported : Shape
    }

    private val BASE_SPELLINGS = mapOf(
        "String" to Base.STRING,
        "Int" to Base.INT, "Integer" to Base.INT,
        "Double" to Base.DOUBLE, "Number" to Base.DOUBLE,
        "Float" to Base.FLOAT, "CGFloat" to Base.FLOAT,
        "Bool" to Base.BOOL, "Boolean" to Base.BOOL,
        "Color" to Base.COLOR,
    )

    /** `string` is `String`, `bool` is `Bool`: lower-case spellings are their capitalised class. */
    private fun base(spelling: String): Base? =
        BASE_SPELLINGS[spelling] ?: BASE_SPELLINGS[spelling.replaceFirstChar { it.uppercaseChar() }]

    private fun shape(spelling: String): Shape {
        base(spelling)?.let { return Shape.Primitive(it) }
        // A closure type is unsupported whatever else it looks like.
        if (spelling.contains("->")) return Shape.Unsupported
        val element = Regex("""^Array\((.+)\)$""").find(spelling)?.groupValues?.get(1)
            ?: Regex("""^\[([^:]+)]$""").find(spelling)?.groupValues?.get(1)
            ?: Regex("""^List<(.+)>$""").find(spelling)?.groupValues?.get(1)
        if (element != null) {
            return base(element.trim())?.let { Shape.ListOf(it) } ?: Shape.Unsupported
        }
        return when (spelling.replaceFirstChar { it.uppercaseChar() }) {
            "Array" -> Shape.AnyPrimitiveList
            "Object", "Hash" -> Shape.PrimitiveMap
            else -> Shape.Unsupported
        }
    }

    private fun optional(declaredClass: String) = declaredClass.trim().endsWith("?")
    private fun core(declaredClass: String) = declaredClass.trim().removeSuffix("?").trim()

    /** Whether an initialState can set a field of this class at all. */
    fun supports(declaredClass: String): Boolean = shape(core(declaredClass)) != Shape.Unsupported

    /**
     * Whether a field of this class appears in the bridge's `data`: one an
     * initialState can set, except a Color (or a list of them) — the Data
     * holds the resolved colour, which cannot be spelled back.
     */
    fun publishes(declaredClass: String): Boolean = when (val s = shape(core(declaredClass))) {
        Shape.Unsupported -> false
        is Shape.Primitive -> s.base != Base.COLOR
        is Shape.ListOf -> s.element != Base.COLOR
        else -> true
    }

    /** Check one value against the class the layout wrote (§10.3, in its order). */
    fun check(value: Any?, declaredClass: String): TestValueVerdict {
        val shape = shape(core(declaredClass))
        if (shape == Shape.Unsupported) return TestValueVerdict.Rejected("unsupported type $declaredClass")
        if (value == null) {
            return if (optional(declaredClass)) TestValueVerdict.Accepted(null)
            else TestValueVerdict.Rejected("null for non-optional $declaredClass")
        }
        val converted: Any? = when (shape) {
            is Shape.Primitive -> primitive(value, shape.base)
            is Shape.ListOf -> (value as? List<*>)?.map { primitive(it ?: return@map MISMATCH, shape.element) }
                ?.takeUnless { it.contains(MISMATCH) }
            Shape.AnyPrimitiveList -> (value as? List<*>)?.takeIf { list -> list.all { isPrimitive(it) } }
                ?.map { plain(it) }
            Shape.PrimitiveMap -> (value as? Map<*, *>)?.takeIf { map -> map.values.all { isPrimitive(it) } }
                ?.entries?.associate { (k, v) -> k.toString() to plain(v) }
            Shape.Unsupported -> null
        }
        return if (converted == null || converted === MISMATCH) TestValueVerdict.Rejected("type mismatch: expected $declaredClass")
        else TestValueVerdict.Accepted(converted)
    }

    private val MISMATCH = Any()

    private fun isPrimitive(v: Any?) = v is String || v is Boolean || (v is Number)

    private fun plain(v: Any?): Any? = if (v is BigDecimal) number(v) else v

    /** A JSON number as the narrowest of Int / Long / Double it fits. */
    private fun number(bd: BigDecimal): Any {
        val stripped = bd.stripTrailingZeros()
        if (stripped.scale() <= 0) {
            runCatching { return stripped.intValueExact() }
            runCatching { return stripped.longValueExact() }
        }
        return bd.toDouble()
    }

    private fun primitive(value: Any, base: Base): Any = when (base) {
        Base.STRING, Base.COLOR -> value as? String ?: MISMATCH
        Base.BOOL -> value as? Boolean ?: MISMATCH
        Base.INT -> integerValue(value) ?: MISMATCH
        Base.DOUBLE -> (value.takeIf { it is Number })?.let { toBigDecimal(it as Number).toDouble() } ?: MISMATCH
        Base.FLOAT -> (value.takeIf { it is Number })?.let { toBigDecimal(it as Number).toFloat() } ?: MISMATCH
    }

    /** A number with no fractional part, within Int (`2.0` is accepted: JavaScript cannot tell it from `2`). */
    private fun integerValue(value: Any): Int? {
        if (value !is Number) return null
        val stripped = toBigDecimal(value).stripTrailingZeros()
        if (stripped.scale() > 0) return null
        return runCatching { stripped.intValueExact() }.getOrNull()
    }

    private fun toBigDecimal(n: Number): BigDecimal = when (n) {
        is BigDecimal -> n
        is Int, is Long, is Short, is Byte -> BigDecimal.valueOf(n.toLong())
        else -> BigDecimal(n.toString())
    }

    /** The Android-only rejection (§10.3): the screen's ViewModel has nothing to apply a value with. */
    const val NO_UPDATE_DATA = "viewModel has no updateData"

    /**
     * Check every key of [values] against [declared] (name → class as the
     * layout wrote it), hand the accepted ones to [update] in one map, and
     * return the rejected. A key that is not declared is `not a data field`.
     * Every key is judged on its own.
     *
     * [resolve] turns an accepted value into what the Data field holds where
     * the check's answer is not yet that (a Color spelling into the colour);
     * it may still reject. With no [update] — a ViewModel without the
     * generated updateData — every key the check accepts is rejected with
     * [NO_UPDATE_DATA] instead of being dropped without a word.
     */
    fun applyAll(
        values: Map<String, Any?>,
        declared: Map<String, String>,
        update: ((Map<String, Any?>) -> Unit)?,
        resolve: (key: String, accepted: Any?) -> TestValueVerdict = { _, v -> TestValueVerdict.Accepted(v) }
    ): List<RejectedKey> {
        val accepted = LinkedHashMap<String, Any?>()
        val rejected = mutableListOf<RejectedKey>()
        for ((key, value) in values) {
            val declaredClass = declared[key]
            if (declaredClass == null) {
                rejected += RejectedKey(key, "not a data field")
                continue
            }
            val verdict = when (val checked = check(value, declaredClass)) {
                is TestValueVerdict.Accepted -> resolve(key, checked.value)
                is TestValueVerdict.Rejected -> checked
            }
            when (verdict) {
                is TestValueVerdict.Accepted ->
                    if (update == null) rejected += RejectedKey(key, NO_UPDATE_DATA) else accepted[key] = verdict.value
                is TestValueVerdict.Rejected -> rejected += RejectedKey(key, verdict.reason)
            }
        }
        if (update != null && accepted.isNotEmpty()) update(accepted)
        return rejected
    }

    /**
     * The [applyAll] `resolve` step for a Color field (or a list of them):
     * the check accepted a spelling, [colorOf] turns it into the colour the
     * Data holds, as updateData resolves a spelling. A spelling that names
     * no colour is `type mismatch: expected <class>`, as on iOS; an accepted
     * null stays null.
     */
    fun resolveColor(accepted: Any?, declaredClass: String, colorOf: (String) -> Any?): TestValueVerdict {
        val mismatch = TestValueVerdict.Rejected("type mismatch: expected $declaredClass")
        return when (accepted) {
            null -> TestValueVerdict.Accepted(null)
            is String -> colorOf(accepted)?.let { TestValueVerdict.Accepted(it) } ?: mismatch
            is List<*> -> {
                val colours = accepted.map { (it as? String)?.let(colorOf) }
                if (colours.any { it == null }) mismatch else TestValueVerdict.Accepted(colours)
            }
            else -> mismatch
        }
    }

    /** The bridge's `data`: the fields whose class is published ([publishes]), by name. */
    fun snapshot(values: Map<String, Any?>, declared: Map<String, String>): Map<String, Any?> =
        values.filterKeys { name -> declared[name]?.let { publishes(it) } == true }

    // -- JSON ---------------------------------------------------------------

    /** Parse JSON text into strings, booleans, [BigDecimal] numbers, lists, maps and null. */
    fun parseJson(text: String): Any? = fromElement(JsonParser.parseString(text))

    private fun fromElement(e: JsonElement): Any? = when {
        e.isJsonNull -> null
        e.isJsonObject -> e.asJsonObject.entrySet().associate { (k, v) -> k to fromElement(v) }
        e.isJsonArray -> e.asJsonArray.map { fromElement(it) }
        e.asJsonPrimitive.isBoolean -> e.asBoolean
        e.asJsonPrimitive.isNumber -> e.asBigDecimal
        else -> e.asString
    }

    /**
     * Serialise a bridge value: maps (keys sorted, as iOS writes them), lists,
     * strings, numbers, booleans, null; anything else as its string.
     */
    fun toJson(value: Any?): String = toElement(value).toString()

    private fun toElement(v: Any?): JsonElement = when (v) {
        null -> JsonNull.INSTANCE
        is String -> JsonPrimitive(v)
        is Boolean -> JsonPrimitive(v)
        is Number -> JsonPrimitive(v)
        is Map<*, *> -> JsonObject().apply {
            v.entries.sortedBy { it.key.toString() }.forEach { (k, value) -> add(k.toString(), toElement(value)) }
        }
        is Iterable<*> -> JsonArray().apply { v.forEach { add(toElement(it)) } }
        is Array<*> -> JsonArray().apply { v.forEach { add(toElement(it)) } }
        else -> JsonPrimitive(v.toString())
    }
}

/**
 * The payload (§10.2), read from the intent extra. One that is not version 1,
 * has no launchId, or is not readable is no payload — the driver then sees no
 * receipt and says so, which is the signal a wrong payload should give.
 */
internal data class TestPayload(val launchId: String, val screens: Map<String, Map<String, Any?>>) {
    companion object {
        fun parse(text: String?): TestPayload? {
            if (text.isNullOrEmpty()) return null
            val root = runCatching { JsonUITestValue.parseJson(text) }.getOrNull() as? Map<*, *> ?: return null
            if ((root["version"] as? Number)?.let { BigDecimal(it.toString()).compareTo(BigDecimal.ONE) == 0 } != true) return null
            val launchId = root["launchId"] as? String ?: return null
            val screens = (root["screens"] as? Map<*, *>)?.entries?.mapNotNull { (id, values) ->
                (values as? Map<*, *>)?.let { m -> id.toString() to m.entries.associate { (k, v) -> k.toString() to v } }
            }?.toMap() ?: emptyMap()
            return TestPayload(launchId, screens)
        }
    }
}

/** What one screen applied in one launch: the bridge's `initialState`. */
internal data class TestReceipt(val applied: List<String>, val rejected: List<RejectedKey>) {
    companion object {
        /** Applied keys are the payload's keys minus the rejected ones; both sorted, as iOS writes them. */
        fun of(values: Map<String, Any?>, rejected: List<RejectedKey>): TestReceipt {
            val rejectedKeys = rejected.map { it.key }.toSet()
            return TestReceipt(values.keys.filter { it !in rejectedKeys }.sorted(), rejected.sortedBy { it.key })
        }
    }
}

/**
 * Consumption per launchId × screenId (§10.3 "once"): a screen built again in
 * the same launch is not applied again, and keeps showing its receipt. Keyed
 * by the launchId the payload carries, so a new launch (a new intent) is
 * never confused with the last one in a reused process. Only the latest
 * launch is kept: nothing is carried from one launch to the next (plan §3.3).
 */
internal object TestConsumption {
    private var launchId: String? = null
    private val receipts = HashMap<String, TestReceipt>()

    @Synchronized
    fun consume(launchId: String, screenId: String, apply: () -> TestReceipt): TestReceipt {
        enter(launchId)
        return receipts.getOrPut(screenId, apply)
    }

    @Synchronized
    fun receipt(launchId: String, screenId: String): TestReceipt? =
        if (this.launchId == launchId) receipts[screenId] else null

    private fun enter(launchId: String) {
        if (this.launchId != launchId) {
            this.launchId = launchId
            receipts.clear()
        }
    }

    @Synchronized
    internal fun clearForTest() {
        launchId = null
        receipts.clear()
    }
}

/** The bridge JSON (§10.4). */
internal fun testBridgeJson(launchId: String?, receipt: TestReceipt?, data: Map<String, Any?>): String =
    JsonUITestValue.toJson(
        mapOf(
            "launchId" to launchId,
            "initialState" to receipt?.let {
                mapOf(
                    "applied" to it.applied,
                    "rejected" to it.rejected.map { r -> mapOf("key" to r.key, "reason" to r.reason) }
                )
            },
            "data" to data
        )
    )

private fun Context.findActivity(): Activity? {
    var c: Context? = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

/**
 * The screen's side of the bridge. Applies the payload's values for [screenId]
 * once (the first composition in a launch), and returns the bridge JSON to put
 * on the screen marker — recomputed whenever [snapshot] reads changed Data, so
 * call it where the screen reads its Data. Null when the gate is closed.
 *
 * [apply] receives the payload's values for this screen and returns the keys
 * it rejected; the generated Data code implements it with
 * [JsonUITestValue.applyAll].
 */
@Composable
fun rememberTestBridge(
    screenId: String,
    snapshot: () -> Map<String, Any?>,
    apply: (Map<String, Any?>) -> List<RejectedKey>
): String? {
    val context = LocalContext.current
    val enabled = remember(context) {
        val debuggable = (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        screenMarkerEnabled(debuggable, !debuggable && screenMarkerMetaDataFlagOf(context))
    }
    if (!enabled) return null
    val payload = remember(context) {
        TestPayload.parse(context.findActivity()?.intent?.getStringExtra(TEST_INITIAL_STATE_EXTRA))
    }
    val launchId = payload?.launchId
    var receipt by remember(screenId, launchId) {
        mutableStateOf(launchId?.let { TestConsumption.receipt(it, screenId) })
    }
    LaunchedEffect(screenId, launchId) {
        val values = payload?.screens?.get(screenId)
        if (launchId != null && values != null) {
            receipt = TestConsumption.consume(launchId, screenId) { TestReceipt.of(values, apply(values)) }
        }
    }
    return testBridgeJson(launchId, receipt, snapshot())
}
