package eu.kanade.tachiyomi;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 12/07/16
 */

import java.util.HashMap;
import java.util.Map;

/**
 * Android compat class
 */
public class R {
    private static Map<Integer, Object> resources = new HashMap<>();
    private static int lastRes = 0;

    public static class string {
        public static int app_name = res("TachiWeb");
    }

    /**
     * Add another resource
     */
    private static int res(String res) {
        int nextRes = lastRes++;
        resources.put(nextRes, res);
        return nextRes;
    }

    /**
     * Get a string resource
     * @param id The id of the resource
     * @return The string resource
     */
    public static String getString(int id) {
        return (String) resources.get(id);
    }
}
