package xyz.nulldev.ts.api.http.download;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.download.DownloadManager;
import eu.kanade.tachiyomi.data.download.model.Download;
import eu.kanade.tachiyomi.data.source.Source;
import eu.kanade.tachiyomi.data.source.SourceManager;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.ChapterUtils;
import xyz.nulldev.ts.util.LeniantParser;

import java.util.Collections;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 28/07/16
 */
public class DownloadChapterRoute extends TachiWebRoute {

    private SourceManager sourceManager = DIReplacement.get().injectSourceManager();
    private DownloadManager downloadManager = DIReplacement.get().injectDownloadManager();

    public DownloadChapterRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        Long mangaId = LeniantParser.parseLong(request.params(":mangaId"));
        Long chapterId = LeniantParser.parseLong(request.params(":chapterId"));
        if (mangaId == null) {
            return error("MangaID must be specified!");
        } else if (chapterId == null) {
            return error("ChapterID must be specified!");
        }
        Manga manga = getLibrary().getManga(mangaId);
        if (manga == null) {
            return error("The specified manga does not exist!");
        }
        Source source = sourceManager.get(manga.getSource());
        if (source == null) {
            throw new IllegalArgumentException();
        }
        Chapter chapter = getLibrary().getChapter(chapterId);
        if (chapter == null) {
            return error("The specified chapter does not exist!");
        }
        boolean delete = "true".equalsIgnoreCase(request.queryParams("delete"));
        Download activeDownload = ChapterUtils.getDownload(downloadManager, chapter);
        if (activeDownload != null) {
            if (delete) {
                return error("This chapter is currently being downloaded!");
            } else {
                return error("This chapter is already being downloaded!");
            }
        }
        boolean isChapterDownloded = downloadManager.isChapterDownloaded(source, manga, chapter);
        if (!delete && isChapterDownloded) {
            return error("This chapter is already downloaded!");
        }
        if (delete && !isChapterDownloded) {
            return error("This chapter is not downloaded!");
        }
        if (delete) {
            boolean wasRunning = downloadManager.isRunning();
            if(wasRunning) {
                downloadManager.destroySubscriptions();
            }
            downloadManager.deleteChapter(source, manga, chapter);
            if(wasRunning) {
                downloadManager.startDownloads();
            }
        } else {
            downloadManager.downloadChapters(manga, Collections.singletonList(chapter));
            downloadManager.startDownloads();
        }
        return success();
    }
}
