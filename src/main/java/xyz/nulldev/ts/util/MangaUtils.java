package xyz.nulldev.ts.util;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.DIReplacement;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 15/07/16
 */
public class MangaUtils {
    public static List<Chapter> getUnreadChapters(Manga manga) {
        return DIReplacement.get().getLibrary().getChapters(manga).stream().filter(chapter -> !chapter.getRead()).collect(Collectors.toList());
    }

    public static int getUnreadCount(Manga manga) {
        return getUnreadChapters(manga).size();
    }
}
