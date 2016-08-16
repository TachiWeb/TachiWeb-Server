package xyz.nulldev.ts.sync.operation.category;

import eu.kanade.tachiyomi.data.database.models.Category;
import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;
import xyz.nulldev.ts.sync.operation.Operation;
import xyz.nulldev.ts.util.OptionalUtils;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public abstract class MangaCategoryOperation extends Operation {
    final String mangaTitle;
    final String mangaUrl;
    final int mangaSource;
    final String categoryName;

    public MangaCategoryOperation(
            String mangaTitle, String mangaUrl, int mangaSource, String categoryName) {
        this.mangaTitle = mangaTitle;
        this.mangaUrl = mangaUrl;
        this.mangaSource = mangaSource;
        this.categoryName = categoryName;
    }

    @Override
    public Conflict tryApply(Library library) {
        Manga manga = library.getManga(mangaUrl, mangaSource);
        Category category =
                OptionalUtils.getOrNull(
                        library.getCategories()
                                .stream()
                                .filter(category1 -> category1.getName().equals(categoryName))
                                .findFirst());
        return tryCategoryApply(manga, category, library);
    }

    public abstract Conflict tryCategoryApply(Manga manga, Category category, Library library);
}
