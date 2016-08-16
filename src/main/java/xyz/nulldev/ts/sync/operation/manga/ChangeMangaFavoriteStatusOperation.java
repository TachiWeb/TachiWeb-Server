package xyz.nulldev.ts.sync.operation.manga;

import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class ChangeMangaFavoriteStatusOperation extends ChangeMangaOperation {
    public static final String NAME = "Change Favorite Status";

    private final boolean newFavoritesStatus;

    public ChangeMangaFavoriteStatusOperation(String mangaTitle, String mangaUrl, int mangaSource, boolean newFavoritesStatus) {
        super(mangaTitle, mangaUrl, mangaSource);
        this.newFavoritesStatus = newFavoritesStatus;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toHumanForm() {
        return mangaTitle + " " + (newFavoritesStatus ? "favorited" : "unfavorited") + ".";
    }

    @Override
    public Conflict tryMangaApply(Manga manga, Library library) {
        manga.setFavorite(newFavoritesStatus);
        return null;
    }
}
