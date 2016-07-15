package xyz.nulldev.ts.util;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 15/07/16
 */
public class StringUtils {
    public static boolean notNullOrEmpty(String s) {
        return s != null && !s.isEmpty();
    }
}
