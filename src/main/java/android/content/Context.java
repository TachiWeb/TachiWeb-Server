//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.content;

import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.Resources.NotFoundException;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewDebug.ExportedProperty;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Android compat class
 * 
 * Allows initializing Context with an exception and overriding some originally final methods.
 */
public abstract class Context {
    public static final String ACCESSIBILITY_SERVICE = "accessibility";
    public static final String ACCOUNT_SERVICE = "account";
    public static final String ACTIVITY_SERVICE = "activity";
    public static final String ALARM_SERVICE = "alarm";
    public static final String APPWIDGET_SERVICE = "appwidget";
    public static final String APP_OPS_SERVICE = "appops";
    public static final String AUDIO_SERVICE = "audio";
    public static final String BATTERY_SERVICE = "batterymanager";
    public static final int BIND_ABOVE_CLIENT = 8;
    public static final int BIND_ADJUST_WITH_ACTIVITY = 128;
    public static final int BIND_ALLOW_OOM_MANAGEMENT = 16;
    public static final int BIND_AUTO_CREATE = 1;
    public static final int BIND_DEBUG_UNBIND = 2;
    public static final int BIND_IMPORTANT = 64;
    public static final int BIND_NOT_FOREGROUND = 4;
    public static final int BIND_WAIVE_PRIORITY = 32;
    public static final String BLUETOOTH_SERVICE = "bluetooth";
    public static final String CAMERA_SERVICE = "camera";
    public static final String CAPTIONING_SERVICE = "captioning";
    public static final String CARRIER_CONFIG_SERVICE = "carrier_config";
    public static final String CLIPBOARD_SERVICE = "clipboard";
    public static final String CONNECTIVITY_SERVICE = "connectivity";
    public static final String CONSUMER_IR_SERVICE = "consumer_ir";
    public static final int CONTEXT_IGNORE_SECURITY = 2;
    public static final int CONTEXT_INCLUDE_CODE = 1;
    public static final int CONTEXT_RESTRICTED = 4;
    public static final String DEVICE_POLICY_SERVICE = "device_policy";
    public static final String DISPLAY_SERVICE = "display";
    public static final String DOWNLOAD_SERVICE = "download";
    public static final String DROPBOX_SERVICE = "dropbox";
    public static final String FINGERPRINT_SERVICE = "fingerprint";
    public static final String INPUT_METHOD_SERVICE = "input_method";
    public static final String INPUT_SERVICE = "input";
    public static final String JOB_SCHEDULER_SERVICE = "jobscheduler";
    public static final String KEYGUARD_SERVICE = "keyguard";
    public static final String LAUNCHER_APPS_SERVICE = "launcherapps";
    public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    public static final String LOCATION_SERVICE = "location";
    public static final String MEDIA_PROJECTION_SERVICE = "media_projection";
    public static final String MEDIA_ROUTER_SERVICE = "media_router";
    public static final String MEDIA_SESSION_SERVICE = "media_session";
    public static final String MIDI_SERVICE = "midi";
    public static final int MODE_APPEND = 32768;
    public static final int MODE_ENABLE_WRITE_AHEAD_LOGGING = 8;
    /** @deprecated */
    @Deprecated
    public static final int MODE_MULTI_PROCESS = 4;
    public static final int MODE_PRIVATE = 0;
    /** @deprecated */
    @Deprecated
    public static final int MODE_WORLD_READABLE = 1;
    /** @deprecated */
    @Deprecated
    public static final int MODE_WORLD_WRITEABLE = 2;
    public static final String NETWORK_STATS_SERVICE = "netstats";
    public static final String NFC_SERVICE = "nfc";
    public static final String NOTIFICATION_SERVICE = "notification";
    public static final String NSD_SERVICE = "servicediscovery";
    public static final String POWER_SERVICE = "power";
    public static final String PRINT_SERVICE = "print";
    public static final String RESTRICTIONS_SERVICE = "restrictions";
    public static final String SEARCH_SERVICE = "search";
    public static final String SENSOR_SERVICE = "sensor";
    public static final String STORAGE_SERVICE = "storage";
    public static final String TELECOM_SERVICE = "telecom";
    public static final String TELEPHONY_SERVICE = "phone";
    public static final String TELEPHONY_SUBSCRIPTION_SERVICE = "telephony_subscription_service";
    public static final String TEXT_SERVICES_MANAGER_SERVICE = "textservices";
    public static final String TV_INPUT_SERVICE = "tv_input";
    public static final String UI_MODE_SERVICE = "uimode";
    public static final String USAGE_STATS_SERVICE = "usagestats";
    public static final String USB_SERVICE = "usb";
    public static final String USER_SERVICE = "user";
    public static final String VIBRATOR_SERVICE = "vibrator";
    public static final String WALLPAPER_SERVICE = "wallpaper";
    public static final String WIFI_P2P_SERVICE = "wifip2p";
    public static final String WIFI_SERVICE = "wifi";
    public static final String WINDOW_SERVICE = "window";

    //Removed RuntimeException in constructor
    public Context() {
    }

    public abstract AssetManager getAssets();

    public abstract Resources getResources();

    public abstract PackageManager getPackageManager();

    public abstract ContentResolver getContentResolver();

    public abstract Looper getMainLooper();

    public abstract Context getApplicationContext();

    public void registerComponentCallbacks(ComponentCallbacks callback) {
        throw new RuntimeException("Stub!");
    }

    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        throw new RuntimeException("Stub!");
    }

    public final CharSequence getText(int resId) {
        throw new RuntimeException("Stub!");
    }
    
    //Modified to not be final
    public String getString(int resId) {
        throw new RuntimeException("Stub!");
    }

    //Modified to not be final
    public String getString(int resId, Object... formatArgs) {
        throw new RuntimeException("Stub!");
    }

    public final int getColor(int id) {
        throw new RuntimeException("Stub!");
    }

    public final Drawable getDrawable(int id) {
        throw new RuntimeException("Stub!");
    }

    public final ColorStateList getColorStateList(int id) {
        throw new RuntimeException("Stub!");
    }

    public abstract void setTheme(int var1);

    @ExportedProperty(
            deepExport = true
    )
    public abstract Theme getTheme();

    public final TypedArray obtainStyledAttributes(int[] attrs) {
        throw new RuntimeException("Stub!");
    }

    public final TypedArray obtainStyledAttributes(int resid, int[] attrs) throws NotFoundException {
        throw new RuntimeException("Stub!");
    }

    public final TypedArray obtainStyledAttributes(AttributeSet set, int[] attrs) {
        throw new RuntimeException("Stub!");
    }

    public final TypedArray obtainStyledAttributes(AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        throw new RuntimeException("Stub!");
    }

    public abstract ClassLoader getClassLoader();

    public abstract String getPackageName();

    public abstract ApplicationInfo getApplicationInfo();

    public abstract String getPackageResourcePath();

    public abstract String getPackageCodePath();

    public abstract SharedPreferences getSharedPreferences(String var1, int var2);

    public abstract FileInputStream openFileInput(String var1) throws FileNotFoundException;

    public abstract FileOutputStream openFileOutput(String var1, int var2) throws FileNotFoundException;

    public abstract boolean deleteFile(String var1);

    public abstract File getFileStreamPath(String var1);

    public abstract File getFilesDir();

    public abstract File getNoBackupFilesDir();

    public abstract File getExternalFilesDir(String var1);

    public abstract File[] getExternalFilesDirs(String var1);

    public abstract File getObbDir();

    public abstract File[] getObbDirs();

    public abstract File getCacheDir();

    public abstract File getCodeCacheDir();

    public abstract File getExternalCacheDir();

    public abstract File[] getExternalCacheDirs();

    public abstract File[] getExternalMediaDirs();

    public abstract String[] fileList();

    public abstract File getDir(String var1, int var2);

    public abstract SQLiteDatabase openOrCreateDatabase(String var1, int var2, CursorFactory var3);

    public abstract SQLiteDatabase openOrCreateDatabase(String var1, int var2, CursorFactory var3, DatabaseErrorHandler var4);

    public abstract boolean deleteDatabase(String var1);

    public abstract File getDatabasePath(String var1);

    public abstract String[] databaseList();

    /** @deprecated */
    @Deprecated
    public abstract Drawable getWallpaper();

    /** @deprecated */
    @Deprecated
    public abstract Drawable peekWallpaper();

    /** @deprecated */
    @Deprecated
    public abstract int getWallpaperDesiredMinimumWidth();

    /** @deprecated */
    @Deprecated
    public abstract int getWallpaperDesiredMinimumHeight();

    /** @deprecated */
    @Deprecated
    public abstract void setWallpaper(Bitmap var1) throws IOException;

    /** @deprecated */
    @Deprecated
    public abstract void setWallpaper(InputStream var1) throws IOException;

    /** @deprecated */
    @Deprecated
    public abstract void clearWallpaper() throws IOException;

    public abstract void startActivity(Intent var1);

    public abstract void startActivity(Intent var1, Bundle var2);

    public abstract void startActivities(Intent[] var1);

    public abstract void startActivities(Intent[] var1, Bundle var2);

    public abstract void startIntentSender(IntentSender var1, Intent var2, int var3, int var4, int var5) throws SendIntentException;

    public abstract void startIntentSender(IntentSender var1, Intent var2, int var3, int var4, int var5, Bundle var6) throws SendIntentException;

    public abstract void sendBroadcast(Intent var1);

    public abstract void sendBroadcast(Intent var1, String var2);

    public abstract void sendOrderedBroadcast(Intent var1, String var2);

    public abstract void sendOrderedBroadcast(Intent var1, String var2, BroadcastReceiver var3, Handler var4, int var5, String var6, Bundle var7);

    public abstract void sendBroadcastAsUser(Intent var1, UserHandle var2);

    public abstract void sendBroadcastAsUser(Intent var1, UserHandle var2, String var3);

    public abstract void sendOrderedBroadcastAsUser(Intent var1, UserHandle var2, String var3, BroadcastReceiver var4, Handler var5, int var6, String var7, Bundle var8);

    /** @deprecated */
    @Deprecated
    public abstract void sendStickyBroadcast(Intent var1);

    /** @deprecated */
    @Deprecated
    public abstract void sendStickyOrderedBroadcast(Intent var1, BroadcastReceiver var2, Handler var3, int var4, String var5, Bundle var6);

    /** @deprecated */
    @Deprecated
    public abstract void removeStickyBroadcast(Intent var1);

    /** @deprecated */
    @Deprecated
    public abstract void sendStickyBroadcastAsUser(Intent var1, UserHandle var2);

    /** @deprecated */
    @Deprecated
    public abstract void sendStickyOrderedBroadcastAsUser(Intent var1, UserHandle var2, BroadcastReceiver var3, Handler var4, int var5, String var6, Bundle var7);

    /** @deprecated */
    @Deprecated
    public abstract void removeStickyBroadcastAsUser(Intent var1, UserHandle var2);

    public abstract Intent registerReceiver(BroadcastReceiver var1, IntentFilter var2);

    public abstract Intent registerReceiver(BroadcastReceiver var1, IntentFilter var2, String var3, Handler var4);

    public abstract void unregisterReceiver(BroadcastReceiver var1);

    public abstract ComponentName startService(Intent var1);

    public abstract boolean stopService(Intent var1);

    public abstract boolean bindService(Intent var1, ServiceConnection var2, int var3);

    public abstract void unbindService(ServiceConnection var1);

    public abstract boolean startInstrumentation(ComponentName var1, String var2, Bundle var3);

    public abstract Object getSystemService(String var1);

    public final <T> T getSystemService(Class<T> serviceClass) {
        throw new RuntimeException("Stub!");
    }

    public abstract String getSystemServiceName(Class<?> var1);

    public abstract int checkPermission(String var1, int var2, int var3);

    public abstract int checkCallingPermission(String var1);

    public abstract int checkCallingOrSelfPermission(String var1);

    public abstract int checkSelfPermission(String var1);

    public abstract void enforcePermission(String var1, int var2, int var3, String var4);

    public abstract void enforceCallingPermission(String var1, String var2);

    public abstract void enforceCallingOrSelfPermission(String var1, String var2);

    public abstract void grantUriPermission(String var1, Uri var2, int var3);

    public abstract void revokeUriPermission(Uri var1, int var2);

    public abstract int checkUriPermission(Uri var1, int var2, int var3, int var4);

    public abstract int checkCallingUriPermission(Uri var1, int var2);

    public abstract int checkCallingOrSelfUriPermission(Uri var1, int var2);

    public abstract int checkUriPermission(Uri var1, String var2, String var3, int var4, int var5, int var6);

    public abstract void enforceUriPermission(Uri var1, int var2, int var3, int var4, String var5);

    public abstract void enforceCallingUriPermission(Uri var1, int var2, String var3);

    public abstract void enforceCallingOrSelfUriPermission(Uri var1, int var2, String var3);

    public abstract void enforceUriPermission(Uri var1, String var2, String var3, int var4, int var5, int var6, String var7);

    public abstract Context createPackageContext(String var1, int var2) throws NameNotFoundException;

    public abstract Context createConfigurationContext(Configuration var1);

    public abstract Context createDisplayContext(Display var1);

    public boolean isRestricted() {
        throw new RuntimeException("Stub!");
    }
}
