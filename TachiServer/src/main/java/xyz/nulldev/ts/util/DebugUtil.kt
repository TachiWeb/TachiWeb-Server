package xyz.nulldev.ts.util

@Deprecated("This method should never be called in the codebase except for debugging purposes")
inline fun <T> printTime(name: String = "", block: () -> T): T {
    val beginTime = System.currentTimeMillis()
    val result = block()
    println("[printTime] ${if (name.isEmpty()) "" else name}${beginTime - System.currentTimeMillis()}ms")
    return result
}