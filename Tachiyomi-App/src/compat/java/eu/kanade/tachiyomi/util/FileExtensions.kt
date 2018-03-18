package eu.kanade.tachiyomi.util

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Returns the uri of a file
 *
 * @param context context of application
 */
fun File.getUriCompat(context: Context)
    = Uri.fromFile(this)
