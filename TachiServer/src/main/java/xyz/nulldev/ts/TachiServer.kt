package xyz.nulldev.ts

import eu.kanade.tachiyomi.App
import xyz.nulldev.androidcompat.AndroidCompat
import xyz.nulldev.androidcompat.AndroidCompatInitializer

/**
 */

fun main(args: Array<String>){
    TachiServer().main(args)
}

class TachiServer {

    val androidCompat by lazy { AndroidCompat() }

    fun main(args: Array<String>) {
        AndroidCompatInitializer().init()
        androidCompat.startApp(App())
    }
}
