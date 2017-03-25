package xyz.nulldev.ts

import xyz.nulldev.androidcompat.AndroidCompatInitializer

/**
 */

fun main(args: Array<String>){
    TachiServer().main(args)
}

class TachiServer {
    fun main(args: Array<String>) {
        AndroidCompatInitializer().init()
    }
}
