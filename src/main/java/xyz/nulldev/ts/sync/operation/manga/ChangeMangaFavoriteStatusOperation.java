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

package xyz.nulldev.ts.sync.operation.manga;

import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class ChangeMangaFavoriteStatusOperation extends ChangeMangaOperation {
    public static final String NAME = "Change Favorite Status";

    private final boolean newFavoritesStatus;

    public ChangeMangaFavoriteStatusOperation(String mangaTitle, String mangaUrl, int mangaSource, boolean newFavoritesStatus) {
        super(mangaTitle, mangaUrl, mangaSource);
        this.newFavoritesStatus = newFavoritesStatus;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toHumanForm() {
        return mangaTitle + " " + (newFavoritesStatus ? "favorited" : "unfavorited") + ".";
    }

    @Override
    public Conflict tryMangaApply(Manga manga, Library library) {
        manga.setFavorite(newFavoritesStatus);
        return null;
    }
}
