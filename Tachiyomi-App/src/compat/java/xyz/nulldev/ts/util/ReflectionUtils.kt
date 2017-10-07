package xyz.nulldev.ts.util

import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.isAccessible

fun KFunction<*>.sigMatches(other: KFunction<*>): Boolean {
    //Match name
    if(name != other.name) return false

    val ourParameters = parameters.filterNot { it.name.isNullOrEmpty() }
    val otherParameters = other.parameters.filterNot { it.name.isNullOrEmpty() }

    //Match parameters
    (0 .. parameters.lastIndex)
            .filter { ourParameters.getOrNull(it)?.type != otherParameters.getOrNull(it)?.type }
            .forEach { return false }

    return true
}

fun KFunction<*>.invokeIn(obj: Any, vararg arguments: Any?) {
    val target = obj::class.functions.find { it.sigMatches(this) }
            ?: throw IllegalStateException("Cannot find function matching requirements!")

    target.isAccessible = true
    target.call(obj, *arguments)
}
