package xyz.nulldev.ts.api

import eu.kanade.tachiyomi.source.SourceManager
import xyz.nulldev.ts.TachiServer
import xyz.nulldev.ts.api.v2.java.Tachiyomi
import xyz.nulldev.ts.ext.kInstance
import java.io.File

class ExtensionTest {
    @org.junit.Before
    fun setUp() {
        TachiServer().initInternals()
    }

    @org.junit.Test
    fun testExtensions() {
        val copied = File.createTempFile("extensiontest", ".apk")
        copied.deleteOnExit()
        copied.outputStream().use { output ->
            this::class.java.getResourceAsStream("/test-extension.apk").use { input ->
                input.copyTo(output)
            }
        }

        // Wait for extension manager to be ready
        Thread.sleep(1000)
        println("BEFORE: ")
        println(kInstance<SourceManager>().getOnlineSources().find {
            it.name == "MangaDex"
        })
        Tachiyomi.extensions.installExternal(copied)
        println("INSTALLED: ")
        println(kInstance<SourceManager>().getOnlineSources().find {
            it.name == "MangaDex"
        })
//        Tachiyomi.extensions.getAll().first().delete()
//        println("REMOVED: ")
//        println(kInstance<SourceManager>().getOnlineSources().find {
//            it.name == "MangaDex"
//        })
    }
}
