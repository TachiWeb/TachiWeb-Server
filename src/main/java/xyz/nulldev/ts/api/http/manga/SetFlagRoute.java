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

package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Manga;
import spark.Request;
import spark.Response;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.api.http.TachiWebRoute;
import xyz.nulldev.ts.util.L;
import xyz.nulldev.ts.util.LeniantParser;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 29/07/16
 */
public class SetFlagRoute extends TachiWebRoute {
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
        MangaFlag flag;
        try {
            flag = MangaFlag.valueOf(L.def(request.params(":flag"), "").toUpperCase());
        } catch (IllegalArgumentException e) {
            return error("Invalid/no flag specified!");
        }
        MangaFlag.FlagState flagState = flag.findFlagState(L.def(request.params(":state"), "").toUpperCase());
        if(flagState == null) {
            return error("Invalid/no flag state specified!");
        }
        flag.set(manga, flagState);
        return success();
    }
}
