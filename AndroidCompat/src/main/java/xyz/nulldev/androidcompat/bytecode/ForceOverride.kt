package xyz.nulldev.androidcompat.bytecode

/**
 * Force override annotation
 */

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ForceOverride(val targetMethod: String)