package com.kotlinjsonui.ciprobe

import android.graphics.Rect
import android.view.View
import java.net.URLDecoder

// Copied into library/ and library-dynamic/ by the newapi job's positive
// control, never committed under src/. Each line calls an API above minSdk 24
// with no version guard, so lint's NewApi must name all three.
internal fun newApiProbe(v: View, xs: MutableList<Int>, s: String): String {
    xs.removeLast() // java.util.List#removeLast, API 35
    v.setSystemGestureExclusionRects(listOf(Rect())) // API 29
    return URLDecoder.decode(s, Charsets.UTF_8) // URLDecoder#decode(String, Charset), API 33
}
