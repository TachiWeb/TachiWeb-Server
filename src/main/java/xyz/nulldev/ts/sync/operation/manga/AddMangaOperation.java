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
public class AddMangaOperation extends Operation {
    public static final String NAME = "Add Manga";

    private final String mangaTitle;
    private final String newMangaUrl;
    private final int newMangaSource;

    public AddMangaOperation(String mangaTitle, String newMangaUrl, int newMangaSource) {
        this.mangaTitle = mangaTitle;
        this.newMangaUrl = newMangaUrl;
        this.newMangaSource = newMangaSource;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Conflict tryApply(Library library) {
        Manga manga = library.getManga(newMangaUrl, newMangaSource);
        if(manga == null) {
            manga = Manga.Companion.create(newMangaUrl, newMangaSource);
            manga.setTitle(mangaTitle);
            long id = library.insertManga(manga);
            manga.setId(id);
        }
        return null;
    }

    @Override
    public String toHumanForm() {
        return "Add new manga: " + mangaTitle + ".";
    }
}
