package xyz.nulldev.ts.api.http.debug;

import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import spark.Request;
import spark.Response;
import spark.Route;
import xyz.nulldev.ts.Library;

import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 12/07/16
 */
public class DebugLibrary implements Route {
    Library library;

    public DebugLibrary(Library library) {
        this.library = library;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append("<table border=\"1\"><tr><th>Manga</th><th>Chapter</th></tr>");
        for (Manga manga : library.getMangas()) {
            List<Chapter> chapters = library.getChapters(manga);
            if (chapters.size() > 0) {
                Chapter first = chapters.remove(0);
                builder.append("<tr><td rowspan=\"")
                        .append(chapters.size() + 1)
                        .append("\">")
                        .append(manga.getTitle())
                        .append(" (")
                        .append(manga.getId())
                        .append(")</td>")
                        .append(buildCell(first))
                        .append("</tr>");
                for(Chapter chapter : chapters) {
                    builder.append("<tr>").append(buildCell(chapter)).append("</tr>");
                }
            }
        }
        builder.append("</table>");
        return builder.toString();
    }

    public String buildCell(Chapter chapter) {
        return "<td><a href=\""
                + buildUrl(chapter)
                + "\">"
                + chapter.getName()
                + " ("
                + chapter.getId()
                + ")</a></td>";
    }

    public String buildUrl(Chapter chapter) {
        //TODO Max pages detection
        return "http://localhost:63343/TachiWeb/reader/reader.html?m=" + chapter.getManga_id() + "&c=" + chapter.getId() + "&mp=999";
    }
}
