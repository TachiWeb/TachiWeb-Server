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

package eu.kanade.tachiyomi.data.preference

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/08/16
 */

class PreferenceKeys {
    val downloadsDirectory = "pref_download_directory_key"
    val downloadThreads = "pref_download_slots_key"
    val reencodeImage = "pref_reencode_image_key"
    val authPassword = "pref_server_password"

    fun sourceUsername(sourceId: Int) = "pref_source_username_$sourceId"

    fun sourcePassword(sourceId: Int) = "pref_source_password_$sourceId"

    fun syncUsername(syncId: Int) = "pref_mangasync_username_$syncId"

    fun syncPassword(syncId: Int) = "pref_mangasync_password_$syncId"
}