package com.kotlinjsonui.dynamic.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.gson.JsonParser
import com.kotlinjsonui.dynamic.DynamicView

/**
 * The text size following, for the video (ticket sjui-label-font-size-ignores-dynamic-type): a layout drawn by the
 * Dynamic face — Labels that follow the system text size (the default), that do not (fontScaling false), that follow
 * up to a cap (maxFontScale 1.3), and a wrapContent SelectBox with a long value (ticket kjui-selectbox-fixed-56dp-...).
 * Test APK only; started from adb (am start) while the system font scale is switched.
 */
class FontScalingDemoActivity : ComponentActivity() {
    private val layout = """
        {"type":"ScrollView","width":"matchParent","height":"matchParent","background":"#FFFFFF","child":[
          {"type":"View","orientation":"vertical","width":"matchParent","paddings":[48,20,24,20],"child":[
            {"type":"Label","text":"文字の大きさへの追従（Android）","fontSize":22,"font":"bold","fontScaling":false,"bottomMargin":20},
            {"type":"Label","text":"追従する（既定）","fontSize":13,"fontColor":"#6B7280","fontScaling":false},
            {"type":"Label","text":"回る順番を決めましょう","fontSize":17,"bottomMargin":16},
            {"type":"Label","text":"追従しない（fontScaling: false）","fontSize":13,"fontColor":"#6B7280","fontScaling":false},
            {"type":"Label","text":"回る順番を決めましょう","fontSize":17,"fontScaling":false,"bottomMargin":16},
            {"type":"Label","text":"上限 1.3 倍（maxFontScale: 1.3）","fontSize":13,"fontColor":"#6B7280","fontScaling":false},
            {"type":"Label","text":"回る順番を決めましょう","fontSize":17,"maxFontScale":1.3,"bottomMargin":24},
            {"type":"Label","text":"SelectBox（height: wrapContent、minHeight 48）","fontSize":13,"fontColor":"#6B7280","fontScaling":false,"bottomMargin":4},
            {"type":"SelectBox","id":"long_select","width":"matchParent","height":"wrapContent","minHeight":48,"fontSize":16,
             "items":["Northwind Trading Company Seasonal Product Collection"],"selectedIndex":0,"cornerRadius":8,"borderColor":"#C4C4C4"}
          ]}
        ]}
    """.trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val json = JsonParser.parseString(layout).asJsonObject
        setContentView(ComposeView(this).apply {
            setContent {
                MaterialTheme {
                    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize().background(Color.White)) {
                        DynamicView(json = json, data = emptyMap())
                    }
                }
            }
        })
    }
}
