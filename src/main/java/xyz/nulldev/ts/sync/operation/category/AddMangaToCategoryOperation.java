package xyz.nulldev.ts.sync.operation.category;

import eu.kanade.tachiyomi.data.database.models.Category;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.database.models.MangaCategory;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;

import java.util.Collections;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class AddMangaToCategoryOperation extends MangaCategoryOperation {
    public static final String NAME = "Add Manga to Category";

    public AddMangaToCategoryOperation(
            String mangaTitle, String mangaUrl, int mangaSource, String categoryName) {
        super(mangaTitle, mangaUrl, mangaSource, categoryName);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toHumanForm() {
        return "Add manga " + mangaTitle + " to category: " + categoryName + ".";
    }

    @Override
    public Conflict tryCategoryApply(Manga manga, Category category, Library library) {
        //Manga does not exist!
        if (manga == null) {
            return new Conflict("The manga " + mangaTitle + " does not exist!");
        }
        if (category == null) {
            return new Conflict("The category " + categoryName + " does not exist!");
        }
        if (library.getCategoriesForManga(manga)
                        .stream()
                        .filter(category1 -> category1.getName().equals(categoryName))
                        .count()
                <= 0) {
            library.insertMangasCategories(Collections.singletonList(MangaCategory.Companion.create(manga, category)));
        }
        return null;
    }
}
