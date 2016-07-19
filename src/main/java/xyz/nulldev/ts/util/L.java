package xyz.nulldev.ts.util;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 18/07/16
 */
public class L {
    /**
     * Return the original if it is not null, otherwise return def.
     * @param original The original
     * @param def The default to be returned if the original is null
     * @param <T> The type of the original and default
     * @return The original if it is not null, otherwise the default
     */
    public static <T> T def(T original, T def) {
        return original != null ? original : def;
    }
}
