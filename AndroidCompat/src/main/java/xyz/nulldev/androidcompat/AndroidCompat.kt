package xyz.nulldev.androidcompat

import android.app.Application
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import xyz.nulldev.androidcompat.androidimpl.CustomContext

class AndroidCompat {

    val context: CustomContext by Kodein.global.lazy.instance()

    fun startApp(application: Application) {
        application.attach(context)
        application.onCreate()
    }
}