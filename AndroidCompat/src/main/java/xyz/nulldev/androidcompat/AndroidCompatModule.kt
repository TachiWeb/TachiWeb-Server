package xyz.nulldev.androidcompat

import android.content.Context
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import xyz.nulldev.androidcompat.androidimpl.CustomContext
import xyz.nulldev.androidcompat.androidimpl.FakePackageManager
import xyz.nulldev.androidcompat.info.ApplicationInfoImpl
import xyz.nulldev.androidcompat.io.AndroidFiles
import xyz.nulldev.androidcompat.pm.PackageController
import xyz.nulldev.androidcompat.service.ServiceSupport

/**
 * AndroidCompatModule
 */

class AndroidCompatModule {
    fun create() = Kodein.Module {
        bind<AndroidFiles>() with singleton { AndroidFiles() }

        bind<ApplicationInfoImpl>() with singleton { ApplicationInfoImpl() }

        bind<ServiceSupport>() with singleton { ServiceSupport() }

        bind<FakePackageManager>() with singleton { FakePackageManager() }

        bind<PackageController>() with singleton { PackageController() }

        //Context
        bind<CustomContext>() with singleton { CustomContext() }
        bind<Context>() with singleton { Kodein.global.instance<CustomContext>() }
    }
}
