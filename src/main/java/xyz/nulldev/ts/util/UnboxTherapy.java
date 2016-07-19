package xyz.nulldev.ts.util;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 11/07/16
 *
 * What happens when nulldev gets pissed off at Java
 */
public class UnboxTherapy {
    /**
     * Unbox an Integer defaulting to -1 if it is null.
     * @param integer The integer to unbox.
     * @return The unboxed integer or -1 if the integer was null.
     */
    public static int unbox(Integer integer) {
        return integer != null ? integer : -1;
    }

    /**
     * Unbox a Long defaulting to -1 if it is null.
     * @param longNum The long to unbox.
     * @return The unboxed long or -1 if the long was null.
     */
    public static long unbox(Long longNum) {
        return longNum != null ? longNum : -1;
    }
}
