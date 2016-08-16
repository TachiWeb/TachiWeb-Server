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
