package com.kotlinjsonui.utils

/**
 * Generates stable cell identifiers for Collection components.
 *
 * Produces `"<primary>_<base36Hash>"` where the hash covers every entry in
 * `data` except the [primaryKey] and the reserved `"cellId"` key. Calling
 * [autoId] repeatedly with the same inputs yields the same string, so it is
 * safe to combine Mode A (VM pre-sets attributes) with Mode B (converter
 * re-applies in body).
 *
 * Kotlin's `Any.hashCode()` contract is JVM-stable across processes, unlike
 * `Swift.Hasher`. Cross-platform cellId equality is still not guaranteed
 * because the hash family differs.
 *
 * Signatures accept `Map<String, Any>` (non-nullable value) to match
 * `CollectionDataSection.CellData.data`, so callers can pass the field
 * directly without a variance cast.
 */
object CellIdGenerator {
    fun autoId(
        data: Map<String, Any>,
        primaryKey: String,
        fallbackIndex: Int
    ): String {
        val primary = data[primaryKey]?.toString() ?: fallbackIndex.toString()
        var hash = 0
        for (key in data.keys.sorted()) {
            if (key == primaryKey || key == "cellId") continue
            hash = 31 * hash + key.hashCode() + hashValue(data[key])
        }
        val encoded = (hash.toLong() and 0xFFFFFFFFL).toString(36)
        return "${primary}_${encoded}"
    }

    /**
     * Returns `data` with a `"cellId"` entry populated. Applied twice it
     * yields the same output because `"cellId"` is excluded from the hash.
     */
    fun enrichCellIds(
        data: List<Map<String, Any>>,
        primaryKey: String,
        onDuplicate: ((List<String>) -> Unit)? = null
    ): List<Map<String, Any>> {
        val mapped = data.mapIndexed { index, item ->
            val id = autoId(item, primaryKey, index)
            item + ("cellId" to id)
        }
        return dedupe(mapped, onDuplicate)
    }

    private fun dedupe(
        cells: List<Map<String, Any>>,
        onDuplicate: ((List<String>) -> Unit)?
    ): List<Map<String, Any>> {
        val seen = mutableMapOf<String, Int>()
        val duplicates = mutableListOf<String>()
        val result = cells.map { item ->
            val id = item["cellId"] as? String ?: return@map item
            val count = (seen[id] ?: 0) + 1
            seen[id] = count
            if (count <= 1) item
            else {
                duplicates.add(id)
                item + ("cellId" to "${id}#${count}")
            }
        }
        if (duplicates.isNotEmpty()) onDuplicate?.invoke(duplicates)
        return result
    }

    private fun hashValue(value: Any?): Int = when (value) {
        null -> 0
        is String, is Int, is Long, is Double, is Float, is Boolean -> value.hashCode()
        is List<*> -> value.fold(0) { acc, item -> 31 * acc + hashValue(item) }
        is Map<*, *> -> {
            @Suppress("UNCHECKED_CAST")
            (value as Map<String, Any?>)
                .entries
                .sortedBy { it.key }
                .fold(0) { acc, (k, v) -> 31 * acc + k.hashCode() + hashValue(v) }
        }
        else -> {
            val typeName = value::class.simpleName ?: ""
            if (typeName.contains("Function") || typeName.contains("Lambda")) 0
            else value.toString().hashCode()
        }
    }
}
