package eu.kanade.tachiyomi.util

import android.content.Context
import android.os.ConnectivityManager
import android.os.PowerManager
import android.support.annotation.StringRes
import android.widget.Toast

fun Context.hasPermission(perm: String) = true

val Context.powerManager: PowerManager
    get() = PowerManager.INSTANCE

val Context.connectivityManager: ConnectivityManager
    get() = ConnectivityManager.INSTANCE

fun Context.toast(@StringRes resource: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(getString(resource), duration)
}

fun Context.toast(text: String?, duration: Int = Toast.LENGTH_SHORT) {
    //TODO Implement
}

