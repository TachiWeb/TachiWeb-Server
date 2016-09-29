/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.nulldev.ts.api.http.download;

import eu.kanade.tachiyomi.data.download.DownloadManager;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.L;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 28/07/16
 */
public class DownloadsOperationRoute extends TachiWebRoute {

    private DownloadManager downloadManager = DIReplacement.get().injectDownloadManager();

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
