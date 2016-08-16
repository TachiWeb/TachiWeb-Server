package xyz.nulldev.ts.sync.operation.chapter;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;
import xyz.nulldev.ts.util.OptionalUtils;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public abstract class ChangeChapterOperation extends ChapterOperation {

    final float chapterNumber;

    public ChangeChapterOperation(
            String mangaTitle, String mangaUrl, int mangaSource, float chapterNumber) {
        super(mangaTitle, mangaUrl, mangaSource);
        this.chapterNumber = chapterNumber;
    }

    @Override
    public final Conflict tryChapterApply(Manga manga, Library library) {
        Chapter chapter =
                OptionalUtils.getOrNull(
                        library.getChapters(manga)
                                .stream()
                                .filter(chapter1 -> chapter1.getChapter_number() == chapterNumber)
                                .findFirst());
        if(chapter == null) {
            return new Conflict("Chapter #" + chapterNumber + " does not exist!");
        }
        return tryChangeChapterApply(manga, chapter, library);
    }

    public abstract Conflict tryChangeChapterApply(Manga manga, Chapter chapter, Library library);
}
