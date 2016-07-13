package xyz.nulldev.ts.api.http.image;

import eu.kanade.tachiyomi.data.database.models.Manga;
import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.util.LeniantParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 13/07/16
 */
public class CoverRoute implements Route {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private Library library;

    public CoverRoute(Library library) {
        this.library = library;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Long mangaId = LeniantParser.parseLong(request.params(":mangaId"));
        if (mangaId == null) {
            return "MangaID must be specified!";
        }
        Manga manga = library.getManga(mangaId);
        if (manga == null) {
            return "The specified manga does not exist!";
        }
        response.header("Access-Control-Allow-Origin", "*");
        String url = manga.getThumbnail_url();
        if (url == null || url.isEmpty()) {
            response.redirect("/img/no-cover.png", 302);
            return null;
        }
        File cacheFile = DIReplacement.get().injectCoverCache().getCoverFile(url);
        File parentFile = cacheFile.getParentFile();
        //Make cache dirs
        parentFile.mkdirs();
        //Download image if it does not exist
        if(!cacheFile.exists()) {
            okhttp3.Response httpResponse = null;
            InputStream stream = null;
            try (FileOutputStream outputStream = new FileOutputStream(cacheFile)) {
                httpResponse =
                        DIReplacement.get()
                                .injectNetworkHelper()
                                .getClient()
                                .newCall(new okhttp3.Request.Builder().url(url).build())
                                .execute();
                stream = httpResponse.body().byteStream();
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n;
                while (-1 != (n = stream.read(buffer))) {
                    outputStream.write(buffer, 0, n);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed to download cover image!";
            } finally {
                if(httpResponse != null) {
                    httpResponse.close();
                }
                if(stream != null) {
                    stream.close();
                }
            }
        }
        //Send cached image
        response.type(Files.probeContentType(cacheFile.toPath()));
        try(FileInputStream stream = new FileInputStream(cacheFile); OutputStream os = response.raw().getOutputStream()) {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n;
            while (-1 != (n = stream.read(buffer))) {
                os.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending cached cover!";
        }
        return null;
    }
}
