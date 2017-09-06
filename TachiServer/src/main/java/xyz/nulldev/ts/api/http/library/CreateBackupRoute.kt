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

package xyz.nulldev.ts.api.http.library

import com.github.salomonbrys.kotson.set
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import eu.kanade.tachiyomi.BackupManagerInternalForwarder
import eu.kanade.tachiyomi.data.backup.BackupCreateService
import eu.kanade.tachiyomi.data.backup.BackupManager
import eu.kanade.tachiyomi.data.backup.models.Backup
import eu.kanade.tachiyomi.data.database.DatabaseHelper
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.kInstanceLazy

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class CreateBackupRoute : TachiWebRoute() {

    private val backupManager: BackupManager by kInstanceLazy()
    private val db: DatabaseHelper by kInstanceLazy()

    private val bmForwarder by lazy {
        BackupManagerInternalForwarder(backupManager)
    }

    override fun handleReq(request: Request, response: Response): Any {
        if ("true".equals(request.queryParams("force-download"), ignoreCase = true)) {
            response.header("Content-Type", "application/octet-stream")
            response.header("Content-Disposition", "attachment; filename=\"backup.json\"")
        }
        // Create root object
        val root = JsonObject()

        // Create information object
        val information = JsonObject()

        // Create manga array
        val mangaEntries = JsonArray()

        // Create category array
        val categoryEntries = JsonArray()

        // Add value's to root
        root[Backup.VERSION] = Backup.CURRENT_VERSION
        root[Backup.MANGAS] = mangaEntries
        root[Backup.CATEGORIES] = categoryEntries

        db.inTransaction {
            // Get manga from database
            val mangas = bmForwarder.getFavoriteManga()

            // Backup library manga and its dependencies
            mangas.forEach { manga ->
                mangaEntries.add(bmForwarder.backupMangaObject(manga, BackupCreateService.BACKUP_ALL))
            }

            // Backup categories
            bmForwarder.backupCategories(categoryEntries)
        }

        return backupManager.parser.toJson(root)
    }
}