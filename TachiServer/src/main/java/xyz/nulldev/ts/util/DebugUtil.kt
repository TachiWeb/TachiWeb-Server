package xyz.nulldev.ts.util

@Deprecated("This method should never be called in the codebase except for debugging purposes")
inline fun <T> printTime(name: String = "", block: () -> T): T {
    val beginTime = System.currentTimeMillis()
    try {
        return block()
    } finally {
        println("[printTime] ${if (name.isEmpty()) "" else name}${System.currentTimeMillis() - beginTime}ms")
    }
}
