package xyz.nulldev.androidcompat

import android.app.Application
import xyz.nulldev.androidcompat.androidimpl.CustomContext

class AndroidCompat {

    val context by lazy {
        CustomContext()
    }

    fun startApp(application: Application) {
        application.attach(context)
        application.onCreate()
    }
}