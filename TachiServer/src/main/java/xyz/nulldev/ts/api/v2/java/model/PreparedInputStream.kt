package xyz.nulldev.ts.api.v2.java.model

import java.io.InputStream

/**
 * A ready-to-open input stream. Once opened, the resulting stream must be closed. If the stream
 * is never opened however, no cleanup is required.
 */
interface PreparedInputStream {
    fun open(): InputStream

     companion object {
         fun from(opener: () -> InputStream) = object : PreparedInputStream {
             override fun open() = opener()
         }
     }
}