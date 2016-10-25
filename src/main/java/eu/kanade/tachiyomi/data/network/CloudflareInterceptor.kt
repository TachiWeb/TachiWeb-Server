/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.kanade.tachiyomi.data.network

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.math.BigDecimal
import javax.script.ScriptEngineManager

//TODO KEEP THIS UPDATED
class CloudflareInterceptor(private val cookies: PersistentCookieStore) : Interceptor {

    //language=RegExp
    private val operationPattern = Regex("""setTimeout\(function\(\)\{\s+(var (?:\w,)+f.+?\r?\n[\s\S]+?a\.value =.+?)\r?\n""")

    //language=RegExp
    private val passPattern = Regex("""name="pass" value="(.+?)"""")

    //language=RegExp
    private val challengePattern = Regex("""name="jschl_vc" value="(\w+)"""")

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        // Check if we already solved a challenge
        if (response.code() != 503 &&
                cookies.get(response.request().url()).any { it.name() == "cf_clearance" }) {
            return response
        }

        // Check if Cloudflare anti-bot is on
        if ("URL=/cdn-cgi/" in response.header("Refresh", "")
                && response.header("Server", "") == "cloudflare-nginx") {
            return chain.proceed(resolveChallenge(response))
        }

        return response
    }

    private fun resolveChallenge(response: Response): Request {
        val engine = ScriptEngineManager().getEngineByName("nashorn")
        val originalRequest = response.request()
        val domain = originalRequest.url().host()
        val content = response.body().string()

        // CloudFlare requires waiting 4 seconds before resolving the challenge
        Thread.sleep(4000)

        val operation = operationPattern.find(content)?.groups?.get(1)?.value
        val challenge = challengePattern.find(content)?.groups?.get(1)?.value
        val pass = passPattern.find(content)?.groups?.get(1)?.value

        if (operation == null || challenge == null || pass == null) {
            throw RuntimeException("Failed resolving Cloudflare challenge")
        }

        val js = operation
                //language=RegExp
                .replace(Regex("""a\.value =(.+?) \+.*"""), "$1")
                //language=RegExp
                .replace(Regex("""\s{3,}[a-z](?: = |\.).+"""), "")
                .replace("\n", "")

        // Duktape can only return strings, so the result has to be converted to string first
        val stringRes = engine.eval("$js.toString()")
        val result: Int
        if (stringRes is String) {
            result = BigDecimal(stringRes).toInt()
        } else if (stringRes is Int) {
            result = stringRes.toInt()
        } else if (stringRes is Double) {
            result = stringRes.toInt()
        } else if (stringRes is Long) {
            result = stringRes.toInt()
        } else {
            throw RuntimeException("Result was not String/Int/Long/Double!")
        }

        val answer = "${result + domain.length}"

        val url = HttpUrl.parse("http://$domain/cdn-cgi/l/chk_jschl").newBuilder()
                .addQueryParameter("jschl_vc", challenge)
                .addQueryParameter("pass", pass)
                .addQueryParameter("jschl_answer", answer)
                .toString()

        val referer = originalRequest.url().toString()
        return GET(url, originalRequest.headers().newBuilder().add("Referer", referer).build())
    }

}