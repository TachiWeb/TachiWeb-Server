package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import eu.kanade.tachiyomi.data.download.DownloadManager;
import eu.kanade.tachiyomi.data.source.Source;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.ChapterUtils;
import xyz.nulldev.ts.util.LeniantParser;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 15/07/16
 */
public class ChaptersRoute extends TachiWebRoute {
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_READ = "read";
    public static final String KEY_LAST_READ = "last_page_read";
    public static final String KEY_CHAPTER_NUMBER = "chapter_number";
    public static final String KEY_DOWNLOAD_STATUS = "download_status";
    public static final String KEY_CONTENT = "content";

    public static final String STATUS_DOWNLOADED = "DOWNLOADED";
    public static final String STATUS_DOWNLOADING = "DOWNLOADING";
    public static final String STATUS_NOT_DOWNLOADED = "NOT_DOWNLOADED";

    private DownloadManager downloadManager = DIReplacement.get().injectDownloadManager();

    public ChaptersRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        Long mangaId = LeniantParser.parseLong(request.params(":mangaId"));
        if (mangaId == null) {
            return error("MangaID must be specified!");
        }
        Manga manga = getLibrary().getManga(mangaId);
        if (manga == null) {
            return error("The specified manga does not exist!");
        }
        Source source = DIReplacement.get().injectSourceManager().get(manga.getSource());
        List<Chapter> chapters = getLibrary().getChapters(manga);
        JSONArray array = new JSONArray();
        for (Chapter chapter : chapters) {
            JSONObject object = new JSONObject();
            object.put(KEY_ID, chapter.getId());
            object.put(KEY_NAME, chapter.getName());
            object.put(KEY_DATE, chapter.getDate_upload());
            object.put(KEY_READ, chapter.getRead());
            object.put(KEY_LAST_READ, chapter.getLast_page_read());
            object.put(KEY_CHAPTER_NUMBER, chapter.getChapter_number());
            if(source != null) {
                object.put(KEY_DOWNLOAD_STATUS, getDownloadStatus(source, manga, chapter));
            }
            array.put(object);
        }
        return success().put(KEY_CONTENT, array);
    }

    private String getDownloadStatus(Source source, Manga manga, Chapter chapter) {
        boolean isDownloaded = downloadManager.isChapterDownloaded(source, manga, chapter);
        if (isDownloaded) {
            return STATUS_DOWNLOADED;
        } else {
            return ChapterUtils.getDownload(downloadManager, chapter) != null ? STATUS_DOWNLOADING : STATUS_NOT_DOWNLOADED;
        }
    }
}
