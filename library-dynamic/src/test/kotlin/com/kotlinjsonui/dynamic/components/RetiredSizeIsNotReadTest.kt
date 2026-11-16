package com.kotlinjsonui.dynamic.components

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Image's (CircleImage drawn as Image's spelling) and CircleView's legacy
 * `size` is retired (jsonui-cli 2.3.22, ruled 2026-10-24): jui's normalizer
 * folds it into width / height, as it folds Indicator's, and kjui_tools no
 * longer reads it. Dynamic read it too — Image put it on after the common
 * modifiers, so it beat a declared width / height where the generated code
 * read it only when neither was declared.
 *
 * A source read, as IndicatorStyleTest's: the bodies are composables.
 * NetworkImage keeps its declared `size` (outside the ruling) and is the
 * control that the read finds a `"size"` where one is.
 */
class RetiredSizeIsNotReadTest {

    private fun code(component: String): String {
        val file = File("src/main/kotlin/com/kotlinjsonui/dynamic/components/$component.kt")
        assertTrue(file.absolutePath, file.isFile)
        return file.readLines().filterNot { it.trimStart().let { t -> t.startsWith("//") || t.startsWith("*") } }
            .joinToString("\n")
    }

    @Test
    fun imageCircleImageAndCircleViewReadNoSize() {
        for (component in listOf("DynamicImageComponent", "DynamicCircleImageComponent", "DynamicCircleViewComponent")) {
            assertFalse(component, code(component).contains("\"size\""))
        }
    }

    @Test
    fun theCirclesKeepTheirDefaultDiameter() {
        // what kjui_tools draws without width / height: CircleImage 48, CircleView DEFAULT_SIZE 20
        assertTrue(code("DynamicCircleImageComponent").contains("val size = 48f"))
        assertTrue(code("DynamicCircleViewComponent").contains("val size = 20f"))
    }

    @Test
    fun control_NetworkImageStillReadsItsDeclaredSize() {
        assertTrue(code("DynamicNetworkImageComponent").contains("\"size\""))
    }
}
