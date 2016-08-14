package xyz.nulldev.ts.sync.operation.manga;

import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class ChangeMangaChapterFlagsOperation extends ChangeMangaOperation {
    public static final String NAME = "Change Flags";

    private final int newFlags;

    public ChangeMangaChapterFlagsOperation(String mangaTitle, String mangaUrl, int mangaSource, int newFlags) {
        super(mangaTitle, mangaUrl, mangaSource);
        this.newFlags = newFlags;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toHumanForm() {
        return "Change flags of " + mangaTitle + " to: " + newFlags + ".";
    }

    @Override
    public Conflict tryMangaApply(Manga manga, Library library) {
        manga.setChapter_flags(newFlags);
        return null;
    }
}
