/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.kanade.tachiyomi.data.mangasync

import android.content.Context
import android.support.annotation.CallSuper
import eu.kanade.tachiyomi.data.database.models.MangaSync
import eu.kanade.tachiyomi.data.network.NetworkHelper
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import okhttp3.OkHttpClient
import rx.Completable
import rx.Observable
import uy.kohesive.injekt.injectLazy

abstract class MangaSyncService(private val context: Context, val id: Int) {

    val preferences: PreferencesHelper by injectLazy()
    val networkService: NetworkHelper by injectLazy()

    open val client: OkHttpClient
        get() = networkService.client

    // Name of the manga sync service to display
    abstract val name: String

    abstract fun login(username: String, password: String): Completable

    open val isLogged: Boolean
        get() = !getUsername().isEmpty() &&
                !getPassword().isEmpty()

    abstract fun add(manga: MangaSync): Observable<MangaSync>

    abstract fun update(manga: MangaSync): Observable<MangaSync>

    abstract fun bind(manga: MangaSync): Observable<MangaSync>

    abstract fun getStatus(status: Int): String

    fun saveCredentials(username: String, password: String) {
        preferences.setMangaSyncCredentials(this, username, password)
    }

    @CallSuper
    open fun logout() {
        preferences.setMangaSyncCredentials(this, "", "")
    }

    fun getUsername() = preferences.mangaSyncUsername(this)

    fun getPassword() = preferences.mangaSyncPassword(this)

}
