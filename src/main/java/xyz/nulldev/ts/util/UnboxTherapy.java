package xyz.nulldev.ts.util;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 11/07/16
 */
/**
 * What happens when nulldev gets pissed off at Java
 */
public class UnboxTherapy {
    public static int unbox(Integer integer) {
        return integer != null ? integer : -1;
    }
    public static long unbox(Long longNum) {
        return longNum != null ? longNum : -1;
    }
}
