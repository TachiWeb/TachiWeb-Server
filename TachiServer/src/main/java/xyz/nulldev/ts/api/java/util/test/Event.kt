package xyz.nulldev.ts.api.java.util.test

sealed class Event(open val message: String) {
    data class Success(override val message: String): Event(message)
    data class Debug(override val message: String): Event(message)
    data class Warning(override val message: String, val exception: Throwable? = null): Event(message)
    data class Error(override val message: String, val exception: Throwable? = null): Event(message)
}