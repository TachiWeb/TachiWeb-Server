package xyz.nulldev.ts.ext

import android.preference.PreferenceManager
import com.f2prateek.rx.preferences.RxSharedPreferences
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.PreferenceKeys as Keys

private val prefs = PreferenceManager.getDefaultSharedPreferences(Kodein.global.instance())
private val rxPrefs = RxSharedPreferences.create(prefs)

val Keys.authPassword get() = "pref_ts_server_password"
fun PreferencesHelper.authPassword() = rxPrefs.getString(Keys.authPassword, "")
