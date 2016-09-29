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
import xyz.nulldev.ts.sync.operation.Operation;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public abstract class ChangeMangaOperation extends Operation {
    final String mangaTitle;
    final String mangaUrl;
    final int mangaSource;

    public ChangeMangaOperation(String mangaTitle, String mangaUrl, int mangaSource) {
        this.mangaTitle = mangaTitle;
        this.mangaUrl = mangaUrl;
        this.mangaSource = mangaSource;
    }

    @Override
    public final Conflict tryApply(Library library) {
        Manga manga = library.getManga(mangaUrl, mangaSource);
        //Manga does not exist!
        if(manga == null) {
            return new Conflict("The manga " + mangaTitle + " does not exist!");
        }
        return tryMangaApply(manga, library);
    }

    public abstract Conflict tryMangaApply(Manga manga, Library library);

    public String getMangaTitle() {
        return mangaTitle;
    }

    public String getMangaUrl() {
        return mangaUrl;
    }

    public int getMangaSource() {
        return mangaSource;
    }
}
