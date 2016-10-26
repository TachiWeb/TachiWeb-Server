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

package eu.kanade.tachiyomi.data.database.models

import java.io.Serializable

/**
 * Object containing the history statistics of a chapter
 */
class History : Serializable {

    /**
     * Id of history object.
     */
    var id: Long? = null

    /**
     * Chapter id of history object.
     */
    var chapter_id: Long = 0

    /**
     * Last time chapter was read in time long format
     */
    var last_read: Long = 0

    /**
     * Total time chapter was read - todo not yet implemented
     */
    var time_read: Long = 0

    companion object {

        /**
         * History constructor
         *
         * @param chapter chapter object
         * @return history object
         */
        fun create(chapter: Chapter): History {
            val history = History()
            history.chapter_id = chapter.id!!
            return history
        }
    }
}
