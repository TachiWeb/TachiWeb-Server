package xyz.nulldev.ts.util;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 12/07/16
 */
public class LeniantParser {
    public static Long parseLong(String string) {
        try {
            return string == null ? null : Long.parseLong(string);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static Integer parseInteger(String string) {
        try {
            return string == null ? null : Integer.parseInt(string);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
