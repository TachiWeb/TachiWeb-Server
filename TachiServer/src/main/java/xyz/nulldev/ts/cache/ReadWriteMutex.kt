package xyz.nulldev.ts.cache

import kotlinx.coroutines.sync.Mutex

class ReadWriteMutex {
    private val readMutex = Mutex(false)
    private val writeMutex = Mutex(false)

    private val refsMutex = Mutex(false)
    @Volatile
    var queuedReadMutexRefs = 0

    suspend fun lockRead() {
        refsMutex.lock()
        if (queuedReadMutexRefs == 0) {
            readMutex.lock()
        }
        queuedReadMutexRefs++
        refsMutex.unlock()
    }

    suspend fun unlockRead() {
        refsMutex.lock()
        if (queuedReadMutexRefs > 0) {
            queuedReadMutexRefs--
        }
        if (queuedReadMutexRefs == 0)
            readMutex.unlock()
        refsMutex.unlock()
    }

    val isReadLocked get() = refsMutex.isLocked || writeMutex.isLocked

    suspend fun lockReadWrite() {
        writeMutex.lock()
        readMutex.lock()
    }

    fun unlockReadWrite() {
        writeMutex.unlock()
        readMutex.unlock()
    }

    val isWriteLocked get() = readMutex.isLocked || isReadLocked
}