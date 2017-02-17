package xyz.nulldev.androidcompat.res;

import xyz.nulldev.androidcompat.info.ApplicationInfoImpl;
import xyz.nulldev.androidcompat.util.KodeinGlobalHelper;

/**
 * BuildConfig compat class.
 */
public class BuildConfigCompat {
    private static ApplicationInfoImpl applicationInfo = KodeinGlobalHelper.Companion.instance(ApplicationInfoImpl.class);

    public static boolean DEBUG = applicationInfo.getDebug();
}
