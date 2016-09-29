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

package xyz.nulldev.ts.api.http.library;

import spark.Request;
import spark.Response;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;

import javax.servlet.MultipartConfigElement;
import java.io.InputStream;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 17/07/16
 */
public class RestoreFromFileRoute extends TachiWebRoute {
    @Override
    public Object handleReq(Request request, Response response) throws Exception {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
        try (InputStream is = request.raw().getPart("uploaded_file").getInputStream()) {
            DIReplacement.get().injectBackupManager().restoreFromStream(is, getLibrary());
        } catch (Exception e) {
            e.printStackTrace();
            return error("Restore failed!");
        }
        return success();
    }
}
