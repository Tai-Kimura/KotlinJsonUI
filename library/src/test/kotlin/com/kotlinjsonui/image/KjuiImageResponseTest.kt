package com.kotlinjsonui.image

import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Which responses are the image (jsonui-cli ticket
 * jui-networkimage-error-response-reported-as-progress): only those are `responded` and
 * counted as progress.
 */
class KjuiImageResponseTest {
    private fun response(code: Int, vararg requestHeaders: Pair<String, String>): Response {
        val request = Request.Builder().url("https://example.com/a.jpg")
            .apply { requestHeaders.forEach { (n, v) -> header(n, v) } }.build()
        return Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(code).message("m").build()
    }

    @Test
    fun aSuccessIsTheImage_aRedirectOrAnErrorIsNot() {
        val codes = listOf(200, 203, 206, 299, 301, 302, 307, 400, 404, 410, 500, 503)
        assertEquals(
            listOf(true, true, true, true, false, false, false, false, false, false, false, false),
            codes.map { response(it).isTheImage() },
        )
    }

    @Test
    fun a304IsTheImageOnlyAsTheAnswerToAConditionalRequest() {
        assertEquals(false, response(304).isTheImage())
        assertEquals(true, response(304, "If-None-Match" to "\"v1\"").isTheImage())
        assertEquals(true, response(304, "If-Modified-Since" to "Sun, 15 Nov 2026 00:00:00 GMT").isTheImage())
    }
}
