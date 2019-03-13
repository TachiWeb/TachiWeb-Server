package xyz.nulldev.ts.api.v3.util

import io.vertx.core.Handler
import io.vertx.core.streams.ReadStream

class EmptyReadStream<T>(var paused: Boolean = false) : ReadStream<T> {
    private var endHandler: Handler<Void>? = null

    override fun fetch(amount: Long): ReadStream<T> {
        return this
    }

    override fun pause(): ReadStream<T> {
        return this
    }

    override fun resume(): ReadStream<T> {
        if (paused) {
            paused = false
            endHandler?.handle(null)
        }
        return this
    }

    override fun handler(handler: Handler<T>?): ReadStream<T> {
        return this
    }

    override fun exceptionHandler(handler: Handler<Throwable>?): ReadStream<T> {
        return this
    }

    override fun endHandler(endHandler: Handler<Void>?): ReadStream<T> {
        this.endHandler = endHandler
        if (!paused) {
            endHandler?.handle(null)
        }
        return this
    }
}