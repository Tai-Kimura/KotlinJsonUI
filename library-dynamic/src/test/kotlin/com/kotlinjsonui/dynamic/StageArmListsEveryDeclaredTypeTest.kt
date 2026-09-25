package com.kotlinjsonui.dynamic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * CommonStagesOnEveryComponentTest (androidTest) holds every declared type to
 * every standard stage — over the types [StageMeasurer]'s list names. This
 * keeps that list from falling behind the declaration: each type the SSoT
 * declares (one generated `<Type>Attributes.kt` apiece, Common aside) must be
 * on it. It runs with the unit suite; the arm itself needs an emulator.
 */
class StageArmListsEveryDeclaredTypeTest {

    @Test
    fun `the stage arm lists every declared type`() {
        val generated = File("src/main/kotlin/com/kotlinjsonui/dynamic/generated")
        val declared = generated.listFiles { f -> f.name.endsWith("Attributes.kt") }.orEmpty()
            .map { it.name.removeSuffix("Attributes.kt") }
            .filter { it != "Common" }
            .toSet()
        // The control: the generated declarations were read at all.
        assertTrue("no declared types under ${generated.absolutePath}", declared.size >= 20)

        val arm = File("src/androidTest/kotlin/com/kotlinjsonui/dynamic/StageMeasurer.kt").readText()
        val list = arm.substringAfter("val TYPES").substringBefore("val EFFECT_EXTRA")
        val listed = Regex("""^\s*"([A-Za-z]+)" to """, RegexOption.MULTILINE)
            .findAll(list).map { it.groupValues[1] }.toSet()
        assertTrue("no types read from StageMeasurer.TYPES", listed.size >= 20)

        assertEquals("declared types the stage arm does not draw", emptySet<String>(), declared - listed)
    }
}
