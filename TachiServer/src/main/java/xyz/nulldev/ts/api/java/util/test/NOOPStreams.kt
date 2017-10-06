package xyz.nulldev.ts.api.java.util.test

import java.io.InputStream
import java.io.OutputStream

class NOOPInputStream(): InputStream() {
    override fun read() = 0
}

class NOOPOutputStream(): OutputStream() {
    override fun write(b: Int) {}
}