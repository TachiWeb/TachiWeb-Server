package xyz.nulldev.androidcompat.res;

import xyz.nulldev.androidcompat.info.ApplicationInfoImpl;
import xyz.nulldev.androidcompat.util.KodeinGlobalHelper;

/**
 * BuildConfig compat class.
 */
public class BuildConfigCompat {
    private static ApplicationInfoImpl applicationInfo = KodeinGlobalHelper.Companion.instance(ApplicationInfoImpl.class);

    public static final boolean DEBUG = applicationInfo.getDebug();

    //We assume application ID = package name
    public static final String APPLICATION_ID = applicationInfo.packageName;
}
