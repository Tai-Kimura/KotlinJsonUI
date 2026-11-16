package com.kotlinjsonui.graphics

/**
 * In-place stack blur (Mario Klingemann's algorithm) — a close visual
 * approximation of a Gaussian blur, replacing the deprecated RenderScript
 * ScriptIntrinsicBlur. Operates on a raw ARGB pixel array so the algorithm
 * has no android.graphics dependency and is unit-testable on the JVM;
 * [com.kotlinjsonui.views.KjuiBlurView] wraps it with Bitmap get/setPixels.
 */
internal object StackBlur {

    /**
     * Blur [pixels] (ARGB, row-major, [width] x [height]) in place.
     * [radius] < 1 or an empty image is a no-op.
     */
    fun blur(pixels: IntArray, width: Int, height: Int, radius: Int) {
        if (radius < 1 || width <= 0 || height <= 0) return
        require(pixels.size >= width * height) {
            "pixel buffer smaller than width*height"
        }
        val w = width
        val h = height

        val div = radius + radius + 1
        val divSum = ((div + 1) shr 1).let { it * it }
        val dv = IntArray(256 * divSum) { it / divSum }

        val r = IntArray(w * h)
        val g = IntArray(w * h)
        val b = IntArray(w * h)
        val a = IntArray(w * h)
        val vmin = IntArray(maxOf(w, h))

        // Horizontal pass
        var yi = 0
        for (y in 0 until h) {
            var rSum = 0; var gSum = 0; var bSum = 0; var aSum = 0
            var rInSum = 0; var gInSum = 0; var bInSum = 0; var aInSum = 0
            var rOutSum = 0; var gOutSum = 0; var bOutSum = 0; var aOutSum = 0
            val stack = Array(div) { IntArray(4) }
            for (i in -radius..radius) {
                val p = pixels[yi + i.coerceIn(0, w - 1)]
                val sir = stack[i + radius]
                sir[0] = (p shr 16) and 0xff
                sir[1] = (p shr 8) and 0xff
                sir[2] = p and 0xff
                sir[3] = (p ushr 24) and 0xff
                val rbs = radius + 1 - kotlin.math.abs(i)
                rSum += sir[0] * rbs; gSum += sir[1] * rbs; bSum += sir[2] * rbs; aSum += sir[3] * rbs
                if (i > 0) {
                    rInSum += sir[0]; gInSum += sir[1]; bInSum += sir[2]; aInSum += sir[3]
                } else {
                    rOutSum += sir[0]; gOutSum += sir[1]; bOutSum += sir[2]; aOutSum += sir[3]
                }
            }
            var stackPointer = radius
            for (x in 0 until w) {
                r[yi] = dv[rSum]; g[yi] = dv[gSum]; b[yi] = dv[bSum]; a[yi] = dv[aSum]
                rSum -= rOutSum; gSum -= gOutSum; bSum -= bOutSum; aSum -= aOutSum
                val stackStart = stackPointer - radius + div
                var sir = stack[stackStart % div]
                rOutSum -= sir[0]; gOutSum -= sir[1]; bOutSum -= sir[2]; aOutSum -= sir[3]
                if (y == 0) vmin[x] = (x + radius + 1).coerceAtMost(w - 1)
                val p = pixels[y * w + vmin[x]]
                sir[0] = (p shr 16) and 0xff
                sir[1] = (p shr 8) and 0xff
                sir[2] = p and 0xff
                sir[3] = (p ushr 24) and 0xff
                rInSum += sir[0]; gInSum += sir[1]; bInSum += sir[2]; aInSum += sir[3]
                rSum += rInSum; gSum += gInSum; bSum += bInSum; aSum += aInSum
                stackPointer = (stackPointer + 1) % div
                sir = stack[stackPointer % div]
                rOutSum += sir[0]; gOutSum += sir[1]; bOutSum += sir[2]; aOutSum += sir[3]
                rInSum -= sir[0]; gInSum -= sir[1]; bInSum -= sir[2]; aInSum -= sir[3]
                yi++
            }
        }

        // Vertical pass
        for (x in 0 until w) {
            var rSum = 0; var gSum = 0; var bSum = 0; var aSum = 0
            var rInSum = 0; var gInSum = 0; var bInSum = 0; var aInSum = 0
            var rOutSum = 0; var gOutSum = 0; var bOutSum = 0; var aOutSum = 0
            val stack = Array(div) { IntArray(4) }
            var yp = -radius * w
            for (i in -radius..radius) {
                val idx = yp.coerceAtLeast(0) + x
                val sir = stack[i + radius]
                sir[0] = r[idx]; sir[1] = g[idx]; sir[2] = b[idx]; sir[3] = a[idx]
                val rbs = radius + 1 - kotlin.math.abs(i)
                rSum += r[idx] * rbs; gSum += g[idx] * rbs; bSum += b[idx] * rbs; aSum += a[idx] * rbs
                if (i > 0) {
                    rInSum += sir[0]; gInSum += sir[1]; bInSum += sir[2]; aInSum += sir[3]
                } else {
                    rOutSum += sir[0]; gOutSum += sir[1]; bOutSum += sir[2]; aOutSum += sir[3]
                }
                if (i < h - 1) yp += w
            }
            var yiV = x
            var stackPointer = radius
            for (y in 0 until h) {
                pixels[yiV] = (dv[aSum] shl 24) or (dv[rSum] shl 16) or (dv[gSum] shl 8) or dv[bSum]
                rSum -= rOutSum; gSum -= gOutSum; bSum -= bOutSum; aSum -= aOutSum
                val stackStart = stackPointer - radius + div
                var sir = stack[stackStart % div]
                rOutSum -= sir[0]; gOutSum -= sir[1]; bOutSum -= sir[2]; aOutSum -= sir[3]
                if (x == 0) vmin[y] = (y + radius + 1).coerceAtMost(h - 1) * w
                val idx = x + vmin[y]
                sir[0] = r[idx]; sir[1] = g[idx]; sir[2] = b[idx]; sir[3] = a[idx]
                rInSum += sir[0]; gInSum += sir[1]; bInSum += sir[2]; aInSum += sir[3]
                rSum += rInSum; gSum += gInSum; bSum += bInSum; aSum += aInSum
                stackPointer = (stackPointer + 1) % div
                sir = stack[stackPointer]
                rOutSum += sir[0]; gOutSum += sir[1]; bOutSum += sir[2]; aOutSum += sir[3]
                rInSum -= sir[0]; gInSum -= sir[1]; bInSum -= sir[2]; aInSum -= sir[3]
                yiV += w
            }
        }
    }
}
