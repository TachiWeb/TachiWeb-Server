package xyz.nulldev.ts.api.java.model.image

import eu.kanade.tachiyomi.data.database.models.Chapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.source.model.Page
import java.io.OutputStream

interface ImageController {
    /**
     * Fetch a manga's cover
     *
     * @param manga The manga
     * @param stream The stream to write the fetched cover to
     * @return The mime-type of the cover image
     */
    fun fetchCover(manga: Manga, stream: OutputStream): String

    /**
     * Fetch a page's image
     *
     * @param chapter The chapter to fetch the page of
     * @param page The page number (0-indexed)
     * @param stream The stream to write the fetched image to
     * @return The mime-type of the image
     */
    fun fetchImage(chapter: Chapter, page: Int, stream: OutputStream): String

    /**
     * Fetch a page's image
     *
     * @param chapter The chapter to fetch the page of
     * @param page The page
     * @param stream The stream to write the fetched image to
     * @return The mime-type of the image
     */
    fun fetchImage(chapter: Chapter, page: Page, stream: OutputStream): String
}