/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.kanade.tachiyomi.util

import okio.BufferedSource
import okio.Okio
import java.io.File
import java.io.OutputStream

/**
 * Saves the given source to a file and closes it. Directories will be created if needed.
 *
 * @param file the file where the source is copied.
 */
//TODO KEEP THIS UPDATED (0a27d4e)
fun BufferedSource.saveTo(file: File) {
    try {
        // Create parent dirs if needed
        file.parentFile.mkdirs()

        // Copy to destination
        saveTo(file.outputStream())
    } catch (e: Exception) {
        close()
        file.delete()
        throw e
    }
}

/**
 * Saves the given source to an output stream and closes both resources.
 *
 * @param stream the stream where the source is copied.
 */
fun BufferedSource.saveTo(stream: OutputStream) {
    use { input ->
        Okio.buffer(Okio.sink(stream)).use {
            it.writeAll(input)
            it.flush()
        }
    }
}

/**
 * Saves the given source to an output stream and closes both resources.
 * The source is expected to be an image, and it may reencode the image.
 *
 * @param stream the stream where the source is copied.
 * @param reencode whether to reencode the image or not.
 */
fun BufferedSource.saveImageTo(stream: OutputStream, reencode: Boolean = false) {
    if (reencode) {
        throw NotImplementedError("Reencoding is not implemented yet!");
    } else {
        saveTo(stream)
    }
}