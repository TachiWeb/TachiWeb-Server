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

package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.util.ChapterSourceSyncKt;
import kotlin.Pair;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.L;
import xyz.nulldev.ts.util.LeniantParser;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 18/07/16
 */
public class UpdateRoute extends TachiWebRoute {

    private static final String KEY_ADDED = "added";
    private static final String KEY_REMOVED = "removed";

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        UpdateType updateType;
        try {
            updateType = UpdateType.valueOf(L.def(request.params(":updateType"), "").toUpperCase());
        } catch(IllegalArgumentException e) {
            return error("Invalid/no update type specified!");
        }
        Long mangaId = LeniantParser.parseLong(request.params(":mangaId"));
        if (mangaId == null) {
            return error("MangaID must be specified!");
        }
        Manga manga = getLibrary().getManga(mangaId);
        if (manga == null) {
            return error("The specified manga does not exist!");
        }
        Source source;
        try {
            source = DIReplacement.get().injectSourceManager().get(manga.getSource());
            if (source == null) {
                throw new IllegalArgumentException();
            }
        } catch (Exception e) {
            return error("This manga's source is not loaded!");
        }
        if(updateType == UpdateType.INFO) {
            //Update manga info
            try {
                manga.copyFrom(source.fetchMangaDetails(manga).toBlocking().first());
                //Update the manga in the library
                getLibrary().insertManga(manga);
            } catch (Exception e) {
                return error("Error updating manga!");
            }
        } else if(updateType == UpdateType.CHAPTERS) {
            //Update manga chapters
            try {
                List<Chapter> chapters = source.fetchChapterList(manga).toBlocking().first();
                if(chapters == null) {
                    throw new NullPointerException();
                }
                //Sync the library chapters with the source chapters
                Pair<Integer, Integer> results = ChapterSourceSyncKt.syncChaptersWithSource(getLibrary(), chapters, manga, source);
                //Return the results in JSON
                JSONObject toReturn = success();
                toReturn.put(KEY_ADDED, results.getFirst());
                toReturn.put(KEY_REMOVED, results.getSecond());
                return toReturn.toString();
            } catch (Exception e) {
                return error("Error updating chapters!");
            }
        } else {
            return error("Null/unimplemented update type!");
        }
        return success();
    }

    /**
     * The type of update to perform
     */
    public enum UpdateType {
        INFO,
        CHAPTERS
    }
}
