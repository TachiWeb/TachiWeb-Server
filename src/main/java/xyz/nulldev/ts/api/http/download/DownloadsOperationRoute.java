package xyz.nulldev.ts.api.http.download;

import eu.kanade.tachiyomi.data.download.DownloadManager;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.L;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 28/07/16
 */
public class DownloadsOperationRoute extends TachiWebRoute {

    private DownloadManager downloadManager = DIReplacement.get().injectDownloadManager();

    public DownloadsOperationRoute(Library library) {
        super(library);
    }

    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        Operation operation;
        try {
            operation = Operation.valueOf(L.def(request.params(":operation"), "").toUpperCase());
        } catch(IllegalArgumentException e) {
            return error("Invalid/no operation specified!");
        }
        if(operation == Operation.PAUSE) {
            if(!downloadManager.isRunning()) {
                return error("Download manager is already paused!");
            }
            downloadManager.destroySubscriptions();
        } else if(operation == Operation.RESUME) {
            if(downloadManager.isRunning()) {
                return error("Download manager is not paused!");
            }
            //I assume we can restart the download manager without reinitialization
            downloadManager.startDownloads();
        } else if(operation == Operation.CLEAR) {
            downloadManager.destroySubscriptions();
            downloadManager.clearQueue();
        }
        return success();
    }

    /**
     * The operation to perform
     */
    public enum Operation {
        PAUSE,
        RESUME,
        CLEAR
    }
}
