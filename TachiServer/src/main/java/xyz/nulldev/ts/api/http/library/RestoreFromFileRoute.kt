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

import eu.kanade.tachiyomi.data.backup.BackupManager
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import xyz.nulldev.ts.api.http.TachiWebRoute
import xyz.nulldev.ts.ext.kInstance
import javax.servlet.MultipartConfigElement

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 30/09/16
 */
class RestoreFromFileRoute : TachiWebRoute() {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleReq(request: Request, response: Response): Any {
        request.attribute("org.eclipse.jetty.multipartConfig", MultipartConfigElement("/tmp"))
        try {
            request.raw().getPart("uploaded_file").inputStream
                    .use { stream -> kInstance<BackupManager>().restoreFromStream(stream) }
        } catch (e: Exception) {
            logger.error("Restore failed!", e)
            return error("Restore failed!")
        }

        return success()
    }
}

