package xyz.nulldev.ts.ext

import java.io.File

fun File.ensureMkdirs() = apply { mkdirs() }