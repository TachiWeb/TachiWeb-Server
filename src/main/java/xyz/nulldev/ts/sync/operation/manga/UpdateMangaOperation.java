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
