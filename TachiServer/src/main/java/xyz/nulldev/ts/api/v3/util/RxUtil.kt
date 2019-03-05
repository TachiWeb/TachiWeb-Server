package xyz.nulldev.ts.api.v3.util

import com.pushtorefresh.storio.operations.PreparedOperation
import com.pushtorefresh.storio.sqlite.operations.get.PreparedGetObject
import kotlinx.coroutines.suspendCancellableCoroutine
import rx.Single
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Single<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        val sub = subscribe({
            continuation.resume(it)
        }, {
            if (!continuation.isCancelled)
                continuation.resumeWithException(it)
        })

        continuation.invokeOnCancellation {
            sub.unsubscribe()
        }
    }
}

suspend fun <T> PreparedOperation<T>.await(): T = asRxSingle().await()
suspend fun <T> PreparedGetObject<T>.await(): T? = asRxSingle().await()
