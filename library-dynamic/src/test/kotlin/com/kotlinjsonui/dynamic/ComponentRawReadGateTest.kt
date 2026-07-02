package com.kotlinjsonui.dynamic

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Structural grep gate for the typed-attribute rollout: dynamic component
 * bodies must not read attributes from the raw gson node. Attribute access
 * goes through the generated `<Component>Attributes.parse` + the TypedAttrs
 * bridge; the only raw reads left on the own node are:
 *
 * - STRUCTURAL_KEYS (child/children/sections/... — tree structure, not
 *   attributes),
 * - `TypedAttrs.rawKey` (declared keys whose accepted value space is wider
 *   than the declared type) and `TypedAttrs.undeclared` (legacy runtime
 *   extras pending definitions backfill) — both named, greppable entry
 *   points.
 *
 * Reads on CHILD nodes (constraint loops etc.) use receivers other than
 * `json`, so they don't trip this gate.
 */
class ComponentRawReadGateTest {

    private val allowedKeys = setOf(
        "child", "children", "data", "shared_data", "include", "style",
        "sections", "cell", "header", "footer", "type"
    )

    @Test
    fun `no raw own-node attribute reads in component bodies`() {
        val componentsDir = File("src/main/kotlin/com/kotlinjsonui/dynamic/components")
        assertTrue("components dir missing: ${componentsDir.absolutePath}", componentsDir.isDirectory)

        val pattern = Regex("""json\.(?:get|has|getAsJsonObject|getAsJsonArray)\("([A-Za-z_]+)"\)""")
        val violations = mutableListOf<String>()

        componentsDir.listFiles { f -> f.extension == "kt" }!!.sorted().forEach { file ->
            file.readLines().forEachIndexed { i, line ->
                pattern.findAll(line).forEach { m ->
                    val key = m.groupValues[1]
                    if (key !in allowedKeys) {
                        violations.add("${file.name}:${i + 1}: raw read of '$key'")
                    }
                }
            }
        }

        assertTrue(
            "raw own-node attribute reads found (use the typed attributes / " +
                "TypedAttrs.rawKey / TypedAttrs.undeclared instead):\n" +
                violations.joinToString("\n"),
            violations.isEmpty()
        )
    }
}
