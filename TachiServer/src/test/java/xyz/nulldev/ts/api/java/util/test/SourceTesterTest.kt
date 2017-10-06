package xyz.nulldev.ts.api.java.util.test

import eu.kanade.tachiyomi.source.online.LoginSource
import xyz.nulldev.ts.TachiServer
import xyz.nulldev.ts.api.java.TachiyomiAPI

class SourceTesterTest {
    @org.junit.Before
    fun setUp() {
        TachiServer().initInternals()
    }

    @org.junit.Test
    fun testSourceTester() {
        val onlineSources = TachiyomiAPI.catalogue.onlineSources.filter {
            //Skip login sources
            it !is LoginSource
        }
        onlineSources.forEachIndexed { index, source ->
            println("Testing source: ${source.name} ${index + 1}/${onlineSources.size}")
            SourceTester().test(source, {
                when(it) {
                    is Event.Success -> println("[OK] ${it.message}")
                    is Event.Debug -> println("[DBG] ${it.message}")
                    is Event.Warning -> {
                        println("[WARN] ${it.message}")
                        it.exception?.printStackTrace()
                    }
                    is Event.Error -> {
                        println("[ERR] ${it.message}")
                        it.exception?.printStackTrace()
                    }
                }
            })
        }
    }
}