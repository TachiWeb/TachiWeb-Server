package timber.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 12/07/16
 *
 * Compat class
 */
public class Timber {
    private static Logger logger = LoggerFactory.getLogger(Timber.class);

    public static String getName() {
        return logger.getName();
    }

    public static boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public static void trace(String s) {
        logger.trace(s);
    }

    public static void trace(String s, Object o) {
        logger.trace(s, o);
    }

    public static void trace(String s, Object o, Object o1) {
        logger.trace(s, o, o1);
    }

    public static void trace(String s, Object... objects) {
        logger.trace(s, objects);
    }

    public static void trace(String s, Throwable throwable) {
        logger.trace(s, throwable);
    }

    public static boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    public static void trace(Marker marker, String s) {
        logger.trace(marker, s);
    }

    public static void trace(Marker marker, String s, Object o) {
        logger.trace(marker, s, o);
    }

    public static void trace(Marker marker, String s, Object o, Object o1) {
        logger.trace(marker, s, o, o1);
    }

    public static void trace(Marker marker, String s, Object... objects) {
        logger.trace(marker, s, objects);
    }

    public static void trace(Marker marker, String s, Throwable throwable) {
        logger.trace(marker, s, throwable);
    }

    public static boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public static void debug(String s) {
        logger.debug(s);
    }

    public static void debug(String s, Object o) {
        logger.debug(s, o);
    }

    public static void debug(String s, Object o, Object o1) {
        logger.debug(s, o, o1);
    }

    public static void debug(String s, Object... objects) {
        logger.debug(s, objects);
    }

    public static void debug(String s, Throwable throwable) {
        logger.debug(s, throwable);
    }

    public static boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    public static void debug(Marker marker, String s) {
        logger.debug(marker, s);
    }

    public static void debug(Marker marker, String s, Object o) {
        logger.debug(marker, s, o);
    }

    public static void debug(Marker marker, String s, Object o, Object o1) {
        logger.debug(marker, s, o, o1);
    }

    public static void debug(Marker marker, String s, Object... objects) {
        logger.debug(marker, s, objects);
    }

    public static void debug(Marker marker, String s, Throwable throwable) {
        logger.debug(marker, s, throwable);
    }

    public static boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public static void info(String s) {
        logger.info(s);
    }

    public static void info(String s, Object o) {
        logger.info(s, o);
    }

    public static void info(String s, Object o, Object o1) {
        logger.info(s, o, o1);
    }

    public static void info(String s, Object... objects) {
        logger.info(s, objects);
    }

    public static void info(String s, Throwable throwable) {
        logger.info(s, throwable);
    }

    public static boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    public static void info(Marker marker, String s) {
        logger.info(marker, s);
    }

    public static void info(Marker marker, String s, Object o) {
        logger.info(marker, s, o);
    }

    public static void info(Marker marker, String s, Object o, Object o1) {
        logger.info(marker, s, o, o1);
    }

    public static void info(Marker marker, String s, Object... objects) {
        logger.info(marker, s, objects);
    }

    public static void info(Marker marker, String s, Throwable throwable) {
        logger.info(marker, s, throwable);
    }

    public static boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public static void warn(String s) {
        logger.warn(s);
    }

    public static void warn(String s, Object o) {
        logger.warn(s, o);
    }

    public static void warn(String s, Object... objects) {
        logger.warn(s, objects);
    }

    public static void warn(String s, Object o, Object o1) {
        logger.warn(s, o, o1);
    }

    public static void warn(String s, Throwable throwable) {
        logger.warn(s, throwable);
    }

    public static boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    public static void warn(Marker marker, String s) {
        logger.warn(marker, s);
    }

    public static void warn(Marker marker, String s, Object o) {
        logger.warn(marker, s, o);
    }

    public static void warn(Marker marker, String s, Object o, Object o1) {
        logger.warn(marker, s, o, o1);
    }

    public static void warn(Marker marker, String s, Object... objects) {
        logger.warn(marker, s, objects);
    }

    public static void warn(Marker marker, String s, Throwable throwable) {
        logger.warn(marker, s, throwable);
    }

    public static boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public static void error(String s) {
        logger.error(s);
    }

    public static void error(String s, Object o) {
        logger.error(s, o);
    }

    public static void error(String s, Object o, Object o1) {
        logger.error(s, o, o1);
    }

    public static void error(String s, Object... objects) {
        logger.error(s, objects);
    }

    public static void error(String s, Throwable throwable) {
        logger.error(s, throwable);
    }

    public static boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    public static void error(Marker marker, String s) {
        logger.error(marker, s);
    }

    public static void error(Marker marker, String s, Object o) {
        logger.error(marker, s, o);
    }

    public static void error(Marker marker, String s, Object o, Object o1) {
        logger.error(marker, s, o, o1);
    }

    public static void error(Marker marker, String s, Object... objects) {
        logger.error(marker, s, objects);
    }

    public static void error(Marker marker, String s, Throwable throwable) {
        logger.error(marker, s, throwable);
    }

    public static void e(String s) {
        logger.error(s);
    }

    public static void e(String s, Object o) {
        logger.error(s, o);
    }

    public static void e(String s, Object o, Object o1) {
        logger.error(s, o, o1);
    }

    public static void e(String s, Object... objects) {
        logger.error(s, objects);
    }

    public static void e(String s, Throwable throwable) {
        logger.error(s, throwable);
    }

    public static void e(Throwable throwable, String s) {
        logger.error(s, throwable);
    }

    public static void e(Marker marker, String s) {
        logger.error(marker, s);
    }

    public static void e(Marker marker, String s, Object o) {
        logger.error(marker, s, o);
    }

    public static void e(Marker marker, String s, Object o, Object o1) {
        logger.error(marker, s, o, o1);
    }

    public static void e(Marker marker, String s, Object... objects) {
        logger.error(marker, s, objects);
    }

    public static void e(Marker marker, String s, Throwable throwable) {
        logger.error(marker, s, throwable);
    }
}
