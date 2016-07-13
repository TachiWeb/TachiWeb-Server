package xyz.nulldev.ts.util;

import java.util.Optional;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 11/07/16
 */
public class OptionalUtils {
    public static <T> T getOrNull(Optional<T> optional) {
        return (optional == null || !optional.isPresent()) ? null : optional.get();
    }
}
