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

package xyz.nulldev.ts.api.http.library;

import eu.kanade.tachiyomi.data.database.models.Category;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.download.DownloadManager;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.SourceManager;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.L;
import xyz.nulldev.ts.util.MangaUtils;

import java.io.File;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 13/07/16
 */
public class LibraryRoute extends TachiWebRoute {
    public static final String KEY_TITLE = "title";
    public static final String KEY_ID = "id";
    public static final String KEY_UNREAD = "unread";
    public static final String KEY_DOWNLOADED = "downloaded";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_CONTENT = "content";

    private DownloadManager downloadManager = DIReplacement.get().injectDownloadManager();
    private SourceManager sourceManager = DIReplacement.get().injectSourceManager();

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        JSONArray array = new JSONArray();
        for(Manga manga : getLibrary().getFavoriteMangas()) {
            Source source = sourceManager.get(manga.getSource());
            JSONObject mangaJson = new JSONObject();
            mangaJson.put(KEY_ID, manga.getId());
            mangaJson.put(KEY_TITLE, manga.getTitle());
            mangaJson.put(KEY_UNREAD, MangaUtils.getUnreadCount(manga));
            if(source != null) {
                mangaJson.put(KEY_DOWNLOADED, isMangaDownloaded(source, manga));
            }
            JSONArray categoriesJson = new JSONArray();
            for(Category category : getLibrary().getCategoriesForManga(manga)) {
                categoriesJson.put(category.getName());
            }
            mangaJson.put(KEY_CATEGORIES, categoriesJson);
            array.put(mangaJson);
        }
        return success().put(KEY_CONTENT, array);
    }

    private boolean isMangaDownloaded(Source source, Manga manga) {
        File mangaDir = downloadManager.getAbsoluteMangaDirectory(source, manga);

        if (mangaDir.exists()) {
            for (File file : L.def(mangaDir.listFiles(), new File[0])) {
                if (file.isDirectory() && L.def(file.listFiles(), new File[0]).length >= 1) {
                    return true;
                }
            }
        }
        return false;
    }
}
