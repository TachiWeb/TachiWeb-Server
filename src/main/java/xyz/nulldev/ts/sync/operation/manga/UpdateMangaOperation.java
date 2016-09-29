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
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.library.LibraryUpdater;
import xyz.nulldev.ts.sync.conflict.Conflict;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/08/16
 */
public class UpdateMangaOperation extends ChangeMangaOperation {
    public static final String NAME = "Update Manga";

    private LibraryUpdater updater;

    public UpdateMangaOperation(String mangaTitle, String mangaUrl, int mangaSource) {
        super(mangaTitle, mangaUrl, mangaSource);
        this.updater = new LibraryUpdater(DIReplacement.get().injectSourceManager());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toHumanForm() {
        return "Update details for manga: " + mangaTitle;
    }

    @Override
    public Conflict tryMangaApply(Manga manga, Library library) {
        updater.updateManga(library, manga);
        return null;
    }
}
