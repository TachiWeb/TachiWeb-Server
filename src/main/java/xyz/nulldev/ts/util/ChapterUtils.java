package xyz.nulldev.ts.util;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.download.DownloadManager;
import eu.kanade.tachiyomi.data.download.model.Download;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.model.Page;
import eu.kanade.tachiyomi.data.source.online.OnlineSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 28/07/16
 */
public class ChapterUtils {

    private static Logger logger = LoggerFactory.getLogger(ChapterUtils.class);

    public static Download getDownload(DownloadManager manager, Chapter chapter) {
        return OptionalUtils.getOrNull(
                manager.getQueue()
                        .stream()
                        .filter(
                                download ->
                                        Objects.equals(
                                                download.getChapter().getId(), chapter.getId()))
                        .findFirst());
    }

    public static List<Page> getPageList(
            DownloadManager downloadManager, Source source, Manga manga, Chapter chapter) {
        List<Page> pageList = downloadManager.getSavedPageList(source, manga, chapter);
        if (pageList == null) {
            //Page list is not downloaded, fetch it now
            if (OnlineSource.class.isAssignableFrom(source.getClass())) {
                OnlineSource casted = (OnlineSource) source;
                //Try getting the page list from the cache first
                pageList =
                        casted.getChapterCache()
                                .getPageListFromCache(casted.getChapterCacheKey(chapter))
                                .onErrorReturn(t -> null)
                                .toBlocking()
                                .first();
                //If it's not in the cache, fetch it from the network and cache it
                if(pageList == null) {
                    try {
                        pageList = casted.fetchPageListFromNetwork(chapter).toBlocking().first();
                    } catch (Exception e) {
                        logger.error("Failed to fetch page list!", e);
                        return null;
                    }
                    //Save page list to cache
                    casted.savePageList(chapter, pageList);
                }
            } else {
                pageList = source.fetchPageList(chapter).toBlocking().first();
            }
        }
        return pageList;
    }
}
