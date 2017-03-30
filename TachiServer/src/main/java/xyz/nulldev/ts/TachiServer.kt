package xyz.nulldev.ts

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import eu.kanade.tachiyomi.App
import xyz.nulldev.androidcompat.AndroidCompat
import xyz.nulldev.androidcompat.AndroidCompatInitializer
import xyz.nulldev.ts.api.http.HttpAPI
import xyz.nulldev.ts.api.http.HttpModule

/**
 */

fun main(args: Array<String>){
    TachiServer().main(args)
}

class TachiServer {

    val androidCompat by lazy { AndroidCompat() }

    fun main(args: Array<String>) {
        //Load Android compatibility dependencies
        AndroidCompatInitializer().init()
        //Load TachiServer and Tachiyomi dependencies
        Kodein.global.addImport(TachiyomiKodeinModule().create())
        //Load HTTP server dependencies
        Kodein.global.addImport(HttpModule().create())

        //Start app
        androidCompat.startApp(App())

        //Start UI server
        TachiWebUIServer().start()

        //Start HTTP API
        HttpAPI().start()
    }
}
