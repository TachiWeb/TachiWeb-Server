package xyz.nulldev.ts.api.v2.http.jvcompat

import io.javalin.UploadedFile
import io.javalin.core.util.Header
import io.javalin.core.util.MultipartUtil
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

fun Context.queryParam(param: String): String? = first.queryParams(param)

fun Context.attribute(attribute: String, value: Any?) = first.attribute(attribute, value)

fun <T> Context.attribute(attribute: String): T = first.attribute(attribute)

/**
 * Gets first uploaded file for the specified name.
 * Requires Apache commons-fileupload library in the classpath.
 */
fun Context.uploadedFile(fileName: String): UploadedFile? = uploadedFiles(fileName).firstOrNull()

/**
 * Gets a list of uploaded files for the specified name.
 * Requires Apache commons-fileupload library in the classpath.
 */
fun Context.uploadedFiles(fileName: String): List<UploadedFile> {
    return if (isMultipartFormData()) MultipartUtil.getUploadedFiles(first.raw(), fileName) else listOf()
}

/**
 * Returns true if request is multipart/form-data.
 */
fun Context.isMultipartFormData(): Boolean = header(Header.CONTENT_TYPE)?.toLowerCase()?.contains("multipart/form-data") == true

/**
 * Sets response header by name and value.
 */
fun Context.header(headerName: String, headerValue: String): Context {
    second.header(headerName, headerValue)
    return this
}

/**
 * Gets a request header by name.
 */
fun Context.header(header: String): String? = first.headers(header)
