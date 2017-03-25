package eu.kanade.tachiyomi;

import xyz.nulldev.androidcompat.res.BuildConfigCompat;

/**
 * BuildConfig compat class.
 */
public class BuildConfig extends BuildConfigCompat {
    //We must override the default one because some of the code in the app believes this to be a constant
    public static final String APPLICATION_ID = "eu.kanade.tachiyomi";
}
