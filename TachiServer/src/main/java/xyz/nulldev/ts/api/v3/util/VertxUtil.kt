package xyz.nulldev.ts.api.v3.util

import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpClientRequest
import io.vertx.core.http.HttpClientResponse
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.http.RequestOptions
import io.vertx.core.streams.ReadStream
import io.vertx.core.streams.WriteStream
import io.vertx.kotlin.core.http.requestOptionsOf
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

fun HttpServerResponse.tryEnd() {
    if (!closed() && !ended())
        end()
}

suspend fun ReadStream<*>.awaitEnd() = suspendCoroutine<Unit> { cont ->
    endHandler {
        cont.resume(Unit)
    }

    exceptionHandler {
        cont.resumeWithException(it)
    }
}

suspend fun ReadStream<*>.resumeAndAwaitEnd() = suspendCoroutine<Unit> { cont ->
    endHandler {
        cont.resume(Unit)
    }

    exceptionHandler {
        cont.resumeWithException(it)
    }

    try {
        resume()
    } catch (t: Throwable) {
        cont.resumeWithException(t)
    }
}

suspend fun HttpClientResponse.awaitBody() = suspendCoroutine<Buffer?> { cont ->
    var resumed = false

    bodyHandler {
        if (!resumed) {
            resumed = true
            cont.resume(it)
        }
    }

    exceptionHandler {
        pause()
        if (!resumed) {
            resumed = true
            cont.resumeWithException(it)
        }
    }

    endHandler {
        if (!resumed) {
            resumed = true
            cont.resume(null)
        }
    }
}

fun <T> WriteStream<T>.watched(block: (T) -> Unit) = object : WriteStream<T> {
    override fun setWriteQueueMaxSize(maxSize: Int): WriteStream<T> {
        this@watched.setWriteQueueMaxSize(maxSize)
        return this
    }

    override fun writeQueueFull(): Boolean {
        return this@watched.writeQueueFull()
    }

    override fun write(data: T): WriteStream<T> {
        block(data)
        this@watched.write(data)
        return this
    }

    override fun end() {
        this@watched.end()
    }

    override fun drainHandler(handler: Handler<Void>?): WriteStream<T> {
        this@watched.drainHandler(handler)
        return this
    }

    override fun exceptionHandler(handler: Handler<Throwable>?): WriteStream<T> {
        this@watched.exceptionHandler(handler)
        return this
    }
}

fun <T> combineWriteStreams(a: WriteStream<T>, b: WriteStream<T>) = object : WriteStream<T> {
    override fun setWriteQueueMaxSize(maxSize: Int): WriteStream<T> {
        a.setWriteQueueMaxSize(maxSize)
        b.setWriteQueueMaxSize(maxSize)
        return this
    }

    override fun writeQueueFull(): Boolean {
        return a.writeQueueFull() || b.writeQueueFull()
    }

    override fun write(data: T): WriteStream<T> {
        a.write(data)
        b.write(data)
        return this
    }

    override fun end() {
        a.end()
        b.end()
    }

    override fun drainHandler(handler: Handler<Void>?): WriteStream<T> {
        a.drainHandler(handler)
        b.drainHandler(handler)
        return this
    }

    override fun exceptionHandler(handler: Handler<Throwable>?): WriteStream<T> {
        a.exceptionHandler(handler)
        b.exceptionHandler(handler)
        return this
    }
}

suspend fun HttpClientRequest.awaitResponse() = suspendCoroutine<HttpClientResponse> { cont ->
    var resumed = false

    handler {
        pause()
        if (!resumed) {
            resumed = true
            cont.resume(it)
        }
    }

    exceptionHandler {
        pause()
        if (!resumed) {
            resumed = true
            cont.resumeWithException(it)
        }
    }

    endHandler {
        pause()
        if (!resumed) {
            resumed = true
            cont.resumeWithException(IllegalStateException("Stream ended with no response!"))
        }
    }

    end()
}

suspend fun <T> ReadStream<T>.awaitSingle() = suspendCoroutine<T> { cont ->
    var resumed = false

    handler {
        pause()
        if (!resumed) {
            resumed = true
            cont.resume(it)
        }
    }

    exceptionHandler {
        pause()
        if (!resumed) {
            resumed = true
            cont.resumeWithException(it)
        }
    }

    endHandler {
        pause()
        if (!resumed) {
            resumed = true
            cont.resumeWithException(IllegalStateException("Stream ended with no response!"))
        }
    }
}

fun URL.asRequestOptions(): RequestOptions {
    val isSSL = this.protocol.equals("https", false)
    return requestOptionsOf(
            host = host,
            port = if (port != -1) port else if (isSSL) 443 else 80,
            ssl = isSSL,
            uri = toURI().toString()
    )
}

/**
 * Reads a specific number of bytes from the input stream
 *
 * @returns The remaining ReadStream (paused) along with the read ByteArray. If there are less data in the stream than the
 *          request amount of bytes, the returned byte array will be smaller
 */
suspend fun ReadStream<Buffer>.readBytes(bytes: Int): Pair<ReadStream<Buffer>, ByteArray> = suspendCoroutine { cont ->
    require(bytes >= 0)

    var remainingBuffer: Buffer? = null
    var remainingHandler: Handler<Buffer>? = null
    var remainingExceptionHandler: Handler<Throwable>? = null
    var remainingEndHandler: Handler<Void>? = null

    val remaining = object : ReadStream<Buffer> {
        var paused = false

        override fun fetch(amount: Long): ReadStream<Buffer> {
            if (amount > 0) {
                if (remainingBuffer != null) remainingHandler?.handle(remainingBuffer)
                remainingBuffer = null
                this@readBytes.fetch(amount - 1)
            }
            return this
        }

        override fun pause(): ReadStream<Buffer> {
            paused = true
            this@readBytes.pause()
            return this
        }

        override fun resume(): ReadStream<Buffer> {
            paused = false
            if (remainingBuffer != null) remainingHandler?.handle(remainingBuffer)
            remainingBuffer = null
            // Handlers could have paused stream again while handling remaining buffer
            if (!paused) this@readBytes.resume()
            return this
        }

        override fun handler(handler: Handler<Buffer>?): ReadStream<Buffer> {
            if (handler != null) remainingHandler = handler
            return this
        }

        override fun exceptionHandler(handler: Handler<Throwable>?): ReadStream<Buffer> {
            if (handler != null) remainingExceptionHandler = handler
            return this
        }

        override fun endHandler(endHandler: Handler<Void>?): ReadStream<Buffer> {
            if (endHandler != null) remainingEndHandler = endHandler
            return this
        }
    }

    val read = Buffer.buffer(bytes)
    var allRead = false

    handler {
        if (allRead) {
            remainingHandler?.handle(it)
        } else {
            val newLength = read.length() + it.length()
            if (newLength >= bytes) {
                val toRead = bytes - read.length()
                read.appendBuffer(it, 0, toRead)
                if (newLength > bytes) remainingBuffer = it.slice(toRead, it.length())
                pause()
                allRead = true
                cont.resume(remaining to read.bytes)
            } else {
                read.appendBuffer(it, 0, it.length())
            }
        }
    }

    exceptionHandler {
        if (!allRead) {
            cont.resumeWithException(it)
            allRead = true
        } else {
            remainingExceptionHandler?.handle(it)
        }
    }

    endHandler {
        if (!allRead) {
            cont.resume(EmptyReadStream<Buffer>() to read.bytes)
            allRead = true
        } else {
            remainingEndHandler?.handle(it)
        }
    }
}
