package xyz.nulldev.ts.api.v2.http.jvcompat

import io.javalin.translator.json.JavalinJsonPlugin
import spark.Request
import spark.Response
import java.io.InputStream
import java.nio.charset.Charset

// Temporary Javalin compatibility shim while API is being rewritten
typealias Context = Pair<Request, Response>

fun Context.json(any: Any): Context {
    return contentType("application/json")
            .result(JavalinJsonPlugin.objectToJsonMapper.map(any))
}

fun Context.result(result: String): Context {
    return result(result.byteInputStream(Charset.forName(second.raw().characterEncoding)))
}

fun Context.result(inputStream: InputStream): Context {
    inputStream.copyTo(second.raw().outputStream)
    return this
}

fun Context.contentType(contentType: String): Context {
    second.type(contentType)
    return this
}

// TODO Compat-unique method
inline fun <reified T> Context.bodyAsClass() =
    bodyAsClass(T::class.java)

fun <T> Context.bodyAsClass(clazz: Class<T>): T {
    return JavalinJsonPlugin.jsonToObjectMapper.map(body(), clazz)
}

fun Context.body(): String = bodyAsBytes().toString(Charset.forName(first.raw().characterEncoding ?: "UTF-8"))

fun Context.bodyAsBytes() = first.raw().inputStream.readBytes()

fun Context.param(param: String): String? = first.params(param)

fun Context.attribute(attribute: String, value: Any?) = first.attribute(attribute, value)

fun <T> Context.attribute(attribute: String): T = first.attribute(attribute)
