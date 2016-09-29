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

package xyz.nulldev.ts.util;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.DIReplacement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 15/07/16
 */
public class MangaUtils {
    public static List<Chapter> getUnreadChapters(Manga manga) {
        return DIReplacement.get().getLibrary().getChapters(manga).stream().filter(chapter -> !chapter.getRead()).collect(Collectors.toList());
    }

    public static int getUnreadCount(Manga manga) {
        return getUnreadChapters(manga).size();
    }
}
