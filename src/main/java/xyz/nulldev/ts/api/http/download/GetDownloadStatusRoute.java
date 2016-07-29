package xyz.nulldev.ts.api.http.download;

import eu.kanade.tachiyomi.data.download.DownloadManager;
import eu.kanade.tachiyomi.data.download.model.Download;
import eu.kanade.tachiyomi.data.source.model.Page;
import org.json.JSONArray;
import org.json.JSONObject;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 28/07/16
 */
public class GetDownloadStatusRoute extends TachiWebRoute {

    public static final String KEY_MANGA_TITLE = "manga_title";
    public static final String KEY_CHAPTER_NAME = "chapter_name";
    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_DOWNLOADED_IMAGES = "downloaded_images";
    public static final String KEY_TOTAL_IMAGES = "total_images";
    public static final String KEY_DOWNLOADS = "downloads";
    public static final String KEY_PAUSED = "paused";

    private DownloadManager downloadManager = DIReplacement.get().injectDownloadManager();

    public GetDownloadStatusRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        JSONObject object = success();
        JSONArray array = new JSONArray();
        for (Download download : downloadManager.getQueue()) {
            JSONObject downloadJson = new JSONObject();
            List<Page> downloadPages = download.getPages();
            float downloadProgressMax;
            float downloadProgress;
            if (downloadPages != null) {
                downloadProgressMax = downloadPages.size() * 100;
                downloadProgress = 0;
                int downloadedImages = 0;
                for(Page page : download.getPages()) {
                    if(page.getStatus() == Page.READY) {
                        downloadedImages++;
                    }
                    downloadProgress += page.getProgress();
                }
                downloadJson.put(KEY_DOWNLOADED_IMAGES, downloadedImages);
                downloadJson.put(KEY_TOTAL_IMAGES, downloadPages.size());
            } else {
                downloadProgressMax = 1;
                downloadProgress = 0;
            }
            downloadJson.put(KEY_PROGRESS, downloadProgress / downloadProgressMax);
            downloadJson.put(KEY_MANGA_TITLE, download.getManga().getTitle());
            downloadJson.put(KEY_CHAPTER_NAME, download.getChapter().getName());
            array.put(downloadJson);
        }
        object.put(KEY_DOWNLOADS, array);
        object.put(KEY_PAUSED, !downloadManager.isRunning());
        return object;
    }
}
