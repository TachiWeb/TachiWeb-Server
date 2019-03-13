package xyz.nulldev.ts.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.base.Throwables
import de.huxhorn.sulky.ulid.ULID
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import org.apache.commons.codec.digest.DigestUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

private const val TMP_FILE_EXT = "tmp"

/**
 * Fast, completely asynchronous disk LFU cache implementation
 *
 * @author nulldev
 */
class AsyncDiskLFUCache(val folder: File,
                        val sizeLimit: Long,
                        ioScope: CoroutineScope = GlobalScope + Dispatchers.IO) : AutoCloseable {
    private val channel = Channel<CacheEvent>(1000)

    private val internalCache = Caffeine.newBuilder()
            .maximumWeight(sizeLimit)
            .removalListener { _: String?, value: CacheEntry?, _ ->
                // Queue cache entry for background eviction
                if (value != null)
                    channel.sendBlocking(CacheEvent.Evict(value))
            }
            .weigher { _: String, value: CacheEntry ->
                value.size.toInt()
            }
            .build<String, CacheEntry>()

    private val inProgressWrites = HashMap<String, ULID.Value>()
    private val inProgressWritesMutex = Mutex(false)

    private val thisThreadULIDGenerator = ThreadLocal.withInitial { ULID() }

    private sealed class CacheEvent {
        data class Commit(val oldFile: File, val hashedKey: String, val ourId: ULID.Value) : CacheEvent()
        data class Evict(val cacheEntry: CacheEntry) : CacheEvent()
        data class Cancel(val oldFile: File) : CacheEvent()
        data class Init(val folderSnapshot: List<File>) : CacheEvent()
    }

    init {
        // Launch background manager
        ioScope.launch {
            channel.consumeEach { entry ->
                when (entry) {
                    is CacheEvent.Commit -> {
                        val newEntryFile = File(folder, "${entry.hashedKey}.${entry.ourId}")
                        Files.move(entry.oldFile.toPath(), newEntryFile.toPath(), StandardCopyOption.REPLACE_EXISTING)

                        internalCache.put(entry.hashedKey, CacheEntry(newEntryFile))
                    }
                    is CacheEvent.Evict -> {
                        entry.cacheEntry.deleting = true
                        entry.cacheEntry.notifyMutex.lock() // Wait for all readers to die
                        entry.cacheEntry.file.delete()
                    }
                    is CacheEvent.Cancel -> {
                        entry.oldFile.delete()
                    }
                    is CacheEvent.Init -> {
                        // Load last state
                        folder.listFiles()?.forEach {
                            // Delete half-written files
                            if (it.name.endsWith(".$TMP_FILE_EXT")) {
                                it.delete()
                            } else {
                                // Load valid entries into cache
                                val (hashedKey, _) = it.name.split(".")
                                internalCache.put(hashedKey, CacheEntry(it))
                            }
                        }
                    }
                }
            }
        }

        // Background init cache
        folder.listFiles()?.let {
            channel.sendBlocking(CacheEvent.Init(it.toList()))
        }

        // Ensure folder exists
        folder.mkdirs()
    }

    private class CacheEntry(val file: File) {
        val size = file.length()

        @Volatile
        var readers = 0
        @Volatile
        var deleting = false
        val notifyMutex = Mutex(false)
    }

    suspend fun get(key: String): Handle? {
        val hashedKey = hashKey(key)
        val entry = internalCache.getIfPresent(hashedKey) ?: return null
        entry.notifyMutex.tryLock()
        entry.readers++
        if (entry.deleting) {
            if (--entry.readers == 0) entry.notifyMutex.unlock()
            return get(hashedKey) // Entry is being deleted, try again for another entry
        }

        return Handle(entry.file) {
            if (--entry.readers == 0) entry.notifyMutex.unlock()
        }
    }

    suspend fun put(key: String): EditHandle {
        val hashedKey = hashKey(key)
        val ourId = thisThreadULIDGenerator.get().nextValue()

        inProgressWritesMutex.withLock {
            inProgressWrites[hashedKey] = ourId
        }

        val tmpFile = File(folder, "$ourId.$TMP_FILE_EXT")

        return EditHandle(tmpFile, onCommit = {
            inProgressWritesMutex.lock()
            if (inProgressWrites[hashedKey] != ourId) {
                inProgressWritesMutex.unlock()

                // Background cancel
                channel.send(CacheEvent.Cancel(tmpFile))
            } else {
                // Commit
                inProgressWrites.remove(hashedKey)
                inProgressWritesMutex.unlock()

                // Background commit
                channel.send(CacheEvent.Commit(tmpFile, hashedKey, ourId))
            }
        }, onCancel = {
            inProgressWritesMutex.lock()
            if (inProgressWrites[hashedKey] == ourId) {
                inProgressWrites.remove(hashedKey)
                inProgressWritesMutex.unlock()
            }

            // Background cancel
            channel.send(CacheEvent.Cancel(tmpFile))
        })
    }

    suspend fun remove(key: String) {
        internalCache.invalidate(hashKey(key))
    }

    private fun hashKey(key: String) = DigestUtils.sha256Hex(key)

    override fun close() {
        channel.close()
    }

    class EditHandle internal constructor(val file: File,
                                          private val onCommit: suspend () -> Unit,
                                          private val onCancel: suspend () -> Unit) {
        private val creationStackTrace = Exception()

        var closed = false

        suspend fun commit() {
            require(!closed)
            closed = true
            onCommit()
        }

        suspend fun cancel() {
            require(!closed)
            closed = true
            onCancel()
        }

        protected fun finalize() {
            if (!closed) {
                KotlinLogging.logger { }.warn {
                    "==> LEAKED ${this::class.qualifiedName}, printing creation stacktrace:\n${Throwables.getStackTraceAsString(creationStackTrace)}"
                }
                runBlocking { onCancel() }
            }
        }
    }

    class Handle internal constructor(val file: File,
                                      val onClose: suspend () -> Unit) {
        private val creationStackTrace = Exception()
        var closed = false

        suspend fun close() {
            require(!closed)
            closed = true
            onClose()
        }

        suspend fun <T> use(block: suspend (File) -> T): T {
            try {
                return block(file)
            } finally {
                if (!closed) onClose()
            }
        }

        suspend fun <T> useBlocking(block: (File) -> T): T {
            try {
                return block(file)
            } finally {
                if (!closed) onClose()
            }
        }

        protected fun finalize() {
            if (!closed) {
                KotlinLogging.logger { }.warn {
                    "==> LEAKED ${this::class.qualifiedName}, printing creation stacktrace:\n${Throwables.getStackTraceAsString(creationStackTrace)}"
                }
                runBlocking { onClose() }
            }
        }
    }
}
