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

package xyz.nulldev.ts.sync.operation.category;

import eu.kanade.tachiyomi.data.database.models.Category;
import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class RemoveMangaFromCategoryOperation extends MangaCategoryOperation {
    public static final String NAME = "Remove Manga from Category";

    public RemoveMangaFromCategoryOperation(String mangaTitle, String mangaUrl, int mangaSource, String categoryName) {
        super(mangaTitle, mangaUrl, mangaSource, categoryName);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toHumanForm() {
        return "Remove manga " + mangaTitle + " from category: " + categoryName + ".";
    }

    @Override
    public Conflict tryCategoryApply(Manga manga, Category category, Library library) {
        if(manga == null || category == null) {
            return null;
        }
        List<Integer> categories = library.getMangaCategories().get(manga.getId());
        if(categories != null) {
            categories.remove(category.getId());
        }
        return null;
    }
}
