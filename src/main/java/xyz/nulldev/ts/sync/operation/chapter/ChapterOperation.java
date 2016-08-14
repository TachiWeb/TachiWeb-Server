package xyz.nulldev.ts.sync.operation.chapter;

import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;
import xyz.nulldev.ts.sync.operation.Operation;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public abstract class ChapterOperation extends Operation {
    final String mangaTitle;
    final String mangaUrl;
    final int mangaSource;

    public ChapterOperation(String mangaTitle, String mangaUrl, int mangaSource) {
        this.mangaTitle = mangaTitle;
        this.mangaUrl = mangaUrl;
        this.mangaSource = mangaSource;
    }

    @Override
    public Conflict tryApply(Library library) {
        Manga manga = library.getManga(mangaUrl, mangaSource);
        //Manga does not exist!
        if(manga == null) {
            return new Conflict("The manga " + mangaTitle + " does not exist!");
        }
        return tryChapterApply(manga, library);
    }

    public abstract Conflict tryChapterApply(Manga manga, Library library);
}
