package xyz.nulldev.ts.sync.operation.manga;

import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class ChangeMangaViewerOperation extends ChangeMangaOperation {
    public static final String NAME = "Change Viewer";

    private final int newViewer;

    public ChangeMangaViewerOperation(String mangaTitle, String mangaUrl, int mangaSource, int newViewer) {
        super(mangaTitle, mangaUrl, mangaSource);
        this.newViewer = newViewer;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toHumanForm() {
        return "Change viewer of " + mangaTitle + " to: " + newViewer + ".";
    }

    @Override
    public Conflict tryMangaApply(Manga manga, Library library) {
        manga.setViewer(newViewer);
        return null;
    }
}
