/*
Copyright 2015 Javier Tomás

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

This file has been heavily modified after being copied from it's source.
 */
package eu.kanade.tachiyomi.data.preference

import android.content.Context
import com.f2prateek.rx.preferences.Preference
import com.f2prateek.rx.preferences.RxSharedPreferences
import eu.kanade.tachiyomi.data.source.online.LoginSource
import xyz.nulldev.ts.files.Files

fun <T> Preference<T>.getOrDefault(): T = get() ?: defaultValue()!!

class PreferencesHelper(context: Context) {

    private val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val loginPrefs = context.getSharedPreferences("lsource_creds", Context.MODE_PRIVATE)
    private val rxPrefs = RxSharedPreferences.create(prefs)

    val keys = PreferenceKeys()

    private val defaultDownloadsDir = Files.getDefaultDownloadsDir()

    fun clear() = prefs.edit().clear().apply()

    fun getSourceUsernameKey(source: LoginSource): String {
        return "${source.id}_username"
    }

    fun getSourcePasswordKey(source: LoginSource): String {
        return "${source.id}_password"
    }

    fun setSourceCredentials(source: LoginSource, username: String, password: String) {
        loginPrefs.edit()
                .putString(getSourceUsernameKey(source), username)
                .putString(getSourcePasswordKey(source), password)
                .commit()
    }

    fun sourceUsername(source: LoginSource): String {
        return loginPrefs.getString(getSourceUsernameKey(source), "")
    }

    fun sourcePassword(source: LoginSource): String {
        return loginPrefs.getString(getSourcePasswordKey(source), "")
    }

    fun downloadsDirectory() = rxPrefs.getString(keys.downloadsDirectory, defaultDownloadsDir.absolutePath)

    //UI Sets this as a string :/
    fun downloadThreads() = rxPrefs.getString(keys.downloadThreads, "3").asObservable().map {
        it.toInt()
    }

    fun reencodeImage() = rxPrefs.getBoolean(keys.reencodeImage, false)
}