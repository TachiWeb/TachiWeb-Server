package xyz.nulldev.ts.sync.operation.chapter;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class ChangeChapterReadingStatusOperation extends ChangeChapterOperation {
    public static final String NAME = "Change Chapter Reading Status";

    private final boolean newReadStatus;
    private final int newLastPageRead;

    public ChangeChapterReadingStatusOperation(String mangaTitle, String mangaUrl, int mangaSource, float chapterNumber, boolean newReadStatus, int newLastPageRead) {
        super(mangaTitle, mangaUrl, mangaSource, chapterNumber);
        this.newReadStatus = newReadStatus;
        this.newLastPageRead = newLastPageRead;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toHumanForm() {
        return "Change reading status of " + mangaTitle + " chapter " + chapterNumber + " to: " + (newReadStatus ? "Read" : "Unread") + ", last page read: " + newLastPageRead + ".";
    }

    @Override
    public Conflict tryChangeChapterApply(Manga manga, Chapter chapter, Library library) {
        chapter.setRead(newReadStatus);
        chapter.setLast_page_read(newLastPageRead);
        return null;
    }
}
