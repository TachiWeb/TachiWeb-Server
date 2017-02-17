//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.content.pm;

import android.annotation.IntDef;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.util.AndroidException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public abstract class PackageManager {
    public static final int COMPONENT_ENABLED_STATE_DEFAULT = 0;
    public static final int COMPONENT_ENABLED_STATE_DISABLED = 2;
    public static final int COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED = 4;
    public static final int COMPONENT_ENABLED_STATE_DISABLED_USER = 3;
    public static final int COMPONENT_ENABLED_STATE_ENABLED = 1;
    public static final int DONT_KILL_APP = 1;
    public static final String EXTRA_VERIFICATION_ID = "android.content.pm.extra.VERIFICATION_ID";
    public static final String EXTRA_VERIFICATION_RESULT = "android.content.pm.extra.VERIFICATION_RESULT";
    public static final String FEATURE_APP_WIDGETS = "android.software.app_widgets";
    public static final String FEATURE_AUDIO_LOW_LATENCY = "android.hardware.audio.low_latency";
    public static final String FEATURE_AUDIO_OUTPUT = "android.hardware.audio.output";
    public static final String FEATURE_AUDIO_PRO = "android.hardware.audio.pro";
    public static final String FEATURE_AUTOMOTIVE = "android.hardware.type.automotive";
    public static final String FEATURE_BACKUP = "android.software.backup";
    public static final String FEATURE_BLUETOOTH = "android.hardware.bluetooth";
    public static final String FEATURE_BLUETOOTH_LE = "android.hardware.bluetooth_le";
    public static final String FEATURE_CAMERA = "android.hardware.camera";
    public static final String FEATURE_CAMERA_ANY = "android.hardware.camera.any";
    public static final String FEATURE_CAMERA_AUTOFOCUS = "android.hardware.camera.autofocus";
    public static final String FEATURE_CAMERA_CAPABILITY_MANUAL_POST_PROCESSING = "android.hardware.camera.capability.manual_post_processing";
    public static final String FEATURE_CAMERA_CAPABILITY_MANUAL_SENSOR = "android.hardware.camera.capability.manual_sensor";
    public static final String FEATURE_CAMERA_CAPABILITY_RAW = "android.hardware.camera.capability.raw";
    public static final String FEATURE_CAMERA_EXTERNAL = "android.hardware.camera.external";
    public static final String FEATURE_CAMERA_FLASH = "android.hardware.camera.flash";
    public static final String FEATURE_CAMERA_FRONT = "android.hardware.camera.front";
    public static final String FEATURE_CAMERA_LEVEL_FULL = "android.hardware.camera.level.full";
    public static final String FEATURE_CONNECTION_SERVICE = "android.software.connectionservice";
    public static final String FEATURE_CONSUMER_IR = "android.hardware.consumerir";
    public static final String FEATURE_DEVICE_ADMIN = "android.software.device_admin";
    public static final String FEATURE_ETHERNET = "android.hardware.ethernet";
    public static final String FEATURE_FAKETOUCH = "android.hardware.faketouch";
    public static final String FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT = "android.hardware.faketouch.multitouch.distinct";
    public static final String FEATURE_FAKETOUCH_MULTITOUCH_JAZZHAND = "android.hardware.faketouch.multitouch.jazzhand";
    public static final String FEATURE_FINGERPRINT = "android.hardware.fingerprint";
    public static final String FEATURE_FREEFORM_WINDOW_MANAGEMENT = "android.software.freeform_window_management";
    public static final String FEATURE_GAMEPAD = "android.hardware.gamepad";
    public static final String FEATURE_HIFI_SENSORS = "android.hardware.sensor.hifi_sensors";
    public static final String FEATURE_HOME_SCREEN = "android.software.home_screen";
    public static final String FEATURE_INPUT_METHODS = "android.software.input_methods";
    public static final String FEATURE_LEANBACK = "android.software.leanback";
    public static final String FEATURE_LIVE_TV = "android.software.live_tv";
    public static final String FEATURE_LIVE_WALLPAPER = "android.software.live_wallpaper";
    public static final String FEATURE_LOCATION = "android.hardware.location";
    public static final String FEATURE_LOCATION_GPS = "android.hardware.location.gps";
    public static final String FEATURE_LOCATION_NETWORK = "android.hardware.location.network";
    public static final String FEATURE_MANAGED_USERS = "android.software.managed_users";
    public static final String FEATURE_MICROPHONE = "android.hardware.microphone";
    public static final String FEATURE_MIDI = "android.software.midi";
    public static final String FEATURE_NFC = "android.hardware.nfc";
    public static final String FEATURE_NFC_HOST_CARD_EMULATION = "android.hardware.nfc.hce";
    public static final String FEATURE_NFC_HOST_CARD_EMULATION_NFCF = "android.hardware.nfc.hcef";
    public static final String FEATURE_OPENGLES_EXTENSION_PACK = "android.hardware.opengles.aep";
    public static final String FEATURE_PICTURE_IN_PICTURE = "android.software.picture_in_picture";
    public static final String FEATURE_PRINTING = "android.software.print";
    public static final String FEATURE_SCREEN_LANDSCAPE = "android.hardware.screen.landscape";
    public static final String FEATURE_SCREEN_PORTRAIT = "android.hardware.screen.portrait";
    public static final String FEATURE_SECURELY_REMOVES_USERS = "android.software.securely_removes_users";
    public static final String FEATURE_SENSOR_ACCELEROMETER = "android.hardware.sensor.accelerometer";
    public static final String FEATURE_SENSOR_AMBIENT_TEMPERATURE = "android.hardware.sensor.ambient_temperature";
    public static final String FEATURE_SENSOR_BAROMETER = "android.hardware.sensor.barometer";
    public static final String FEATURE_SENSOR_COMPASS = "android.hardware.sensor.compass";
    public static final String FEATURE_SENSOR_GYROSCOPE = "android.hardware.sensor.gyroscope";
    public static final String FEATURE_SENSOR_HEART_RATE = "android.hardware.sensor.heartrate";
    public static final String FEATURE_SENSOR_HEART_RATE_ECG = "android.hardware.sensor.heartrate.ecg";
    public static final String FEATURE_SENSOR_LIGHT = "android.hardware.sensor.light";
    public static final String FEATURE_SENSOR_PROXIMITY = "android.hardware.sensor.proximity";
    public static final String FEATURE_SENSOR_RELATIVE_HUMIDITY = "android.hardware.sensor.relative_humidity";
    public static final String FEATURE_SENSOR_STEP_COUNTER = "android.hardware.sensor.stepcounter";
    public static final String FEATURE_SENSOR_STEP_DETECTOR = "android.hardware.sensor.stepdetector";
    public static final String FEATURE_SIP = "android.software.sip";
    public static final String FEATURE_SIP_VOIP = "android.software.sip.voip";
    public static final String FEATURE_TELEPHONY = "android.hardware.telephony";
    public static final String FEATURE_TELEPHONY_CDMA = "android.hardware.telephony.cdma";
    public static final String FEATURE_TELEPHONY_GSM = "android.hardware.telephony.gsm";
    /** @deprecated */
    @Deprecated
    public static final String FEATURE_TELEVISION = "android.hardware.type.television";
    public static final String FEATURE_TOUCHSCREEN = "android.hardware.touchscreen";
    public static final String FEATURE_TOUCHSCREEN_MULTITOUCH = "android.hardware.touchscreen.multitouch";
    public static final String FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT = "android.hardware.touchscreen.multitouch.distinct";
    public static final String FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND = "android.hardware.touchscreen.multitouch.jazzhand";
    public static final String FEATURE_USB_ACCESSORY = "android.hardware.usb.accessory";
    public static final String FEATURE_USB_HOST = "android.hardware.usb.host";
    public static final String FEATURE_VERIFIED_BOOT = "android.software.verified_boot";
    public static final String FEATURE_VR_MODE = "android.software.vr.mode";
    public static final String FEATURE_VR_MODE_HIGH_PERFORMANCE = "android.hardware.vr.high_performance";
    public static final String FEATURE_VULKAN_HARDWARE_LEVEL = "android.hardware.vulkan.level";
    public static final String FEATURE_VULKAN_HARDWARE_VERSION = "android.hardware.vulkan.version";
    public static final String FEATURE_WATCH = "android.hardware.type.watch";
    public static final String FEATURE_WEBVIEW = "android.software.webview";
    public static final String FEATURE_WIFI = "android.hardware.wifi";
    public static final String FEATURE_WIFI_DIRECT = "android.hardware.wifi.direct";
    public static final int GET_ACTIVITIES = 1;
    public static final int GET_CONFIGURATIONS = 16384;
    /** @deprecated */
    @Deprecated
    public static final int GET_DISABLED_COMPONENTS = 512;
    /** @deprecated */
    @Deprecated
    public static final int GET_DISABLED_UNTIL_USED_COMPONENTS = 32768;
    public static final int GET_GIDS = 256;
    public static final int GET_INSTRUMENTATION = 16;
    public static final int GET_INTENT_FILTERS = 32;
    public static final int GET_META_DATA = 128;
    public static final int GET_PERMISSIONS = 4096;
    public static final int GET_PROVIDERS = 8;
    public static final int GET_RECEIVERS = 2;
    public static final int GET_RESOLVED_FILTER = 64;
    public static final int GET_SERVICES = 4;
    public static final int GET_SHARED_LIBRARY_FILES = 1024;
    public static final int GET_SIGNATURES = 64;
    /** @deprecated */
    @Deprecated
    public static final int GET_UNINSTALLED_PACKAGES = 8192;
    public static final int GET_URI_PERMISSION_PATTERNS = 2048;
    public static final int MATCH_ALL = 131072;
    public static final int MATCH_DEFAULT_ONLY = 65536;
    public static final int MATCH_DIRECT_BOOT_AWARE = 524288;
    public static final int MATCH_DIRECT_BOOT_UNAWARE = 262144;
    public static final int MATCH_DISABLED_COMPONENTS = 512;
    public static final int MATCH_DISABLED_UNTIL_USED_COMPONENTS = 32768;
    public static final int MATCH_SYSTEM_ONLY = 1048576;
    public static final int MATCH_UNINSTALLED_PACKAGES = 8192;
    public static final long MAXIMUM_VERIFICATION_TIMEOUT = 3600000L;
    public static final int PERMISSION_DENIED = -1;
    public static final int PERMISSION_GRANTED = 0;
    public static final int SIGNATURE_FIRST_NOT_SIGNED = -1;
    public static final int SIGNATURE_MATCH = 0;
    public static final int SIGNATURE_NEITHER_SIGNED = 1;
    public static final int SIGNATURE_NO_MATCH = -3;
    public static final int SIGNATURE_SECOND_NOT_SIGNED = -2;
    public static final int SIGNATURE_UNKNOWN_PACKAGE = -4;
    public static final int VERIFICATION_ALLOW = 1;
    public static final int VERIFICATION_REJECT = -1;

    public PackageManager() {
        throw new RuntimeException("Stub!");
    }

    public abstract PackageInfo getPackageInfo(String var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract String[] currentToCanonicalPackageNames(String[] var1);

    public abstract String[] canonicalToCurrentPackageNames(String[] var1);

    public abstract Intent getLaunchIntentForPackage(String var1);

    public abstract Intent getLeanbackLaunchIntentForPackage(String var1);

    public abstract int[] getPackageGids(String var1) throws PackageManager.NameNotFoundException;

    public abstract int[] getPackageGids(String var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract int getPackageUid(String var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract PermissionInfo getPermissionInfo(String var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract List<PermissionInfo> queryPermissionsByGroup(String var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract PermissionGroupInfo getPermissionGroupInfo(String var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract List<PermissionGroupInfo> getAllPermissionGroups(int var1);

    public abstract ApplicationInfo getApplicationInfo(String var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract ActivityInfo getActivityInfo(ComponentName var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract ActivityInfo getReceiverInfo(ComponentName var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract ServiceInfo getServiceInfo(ComponentName var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract ProviderInfo getProviderInfo(ComponentName var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract List<PackageInfo> getInstalledPackages(int var1);

    public abstract List<PackageInfo> getPackagesHoldingPermissions(String[] var1, int var2);

    public abstract int checkPermission(String var1, String var2);

    public abstract boolean isPermissionRevokedByPolicy(String var1, String var2);

    public abstract boolean addPermission(PermissionInfo var1);

    public abstract boolean addPermissionAsync(PermissionInfo var1);

    public abstract void removePermission(String var1);

    public abstract int checkSignatures(String var1, String var2);

    public abstract int checkSignatures(int var1, int var2);

    public abstract String[] getPackagesForUid(int var1);

    public abstract String getNameForUid(int var1);

    public abstract List<ApplicationInfo> getInstalledApplications(int var1);

    public abstract String[] getSystemSharedLibraryNames();

    public abstract FeatureInfo[] getSystemAvailableFeatures();

    public abstract boolean hasSystemFeature(String var1);

    public abstract boolean hasSystemFeature(String var1, int var2);

    public abstract ResolveInfo resolveActivity(Intent var1, int var2);

    public abstract List<ResolveInfo> queryIntentActivities(Intent var1, int var2);

    public abstract List<ResolveInfo> queryIntentActivityOptions(ComponentName var1, Intent[] var2, Intent var3, int var4);

    public abstract List<ResolveInfo> queryBroadcastReceivers(Intent var1, int var2);

    public abstract ResolveInfo resolveService(Intent var1, int var2);

    public abstract List<ResolveInfo> queryIntentServices(Intent var1, int var2);

    public abstract List<ResolveInfo> queryIntentContentProviders(Intent var1, int var2);

    public abstract ProviderInfo resolveContentProvider(String var1, int var2);

    public abstract List<ProviderInfo> queryContentProviders(String var1, int var2, int var3);

    public abstract InstrumentationInfo getInstrumentationInfo(ComponentName var1, int var2) throws PackageManager.NameNotFoundException;

    public abstract List<InstrumentationInfo> queryInstrumentation(String var1, int var2);

    public abstract Drawable getDrawable(String var1, int var2, ApplicationInfo var3);

    public abstract Drawable getActivityIcon(ComponentName var1) throws PackageManager.NameNotFoundException;

    public abstract Drawable getActivityIcon(Intent var1) throws PackageManager.NameNotFoundException;

    public abstract Drawable getActivityBanner(ComponentName var1) throws PackageManager.NameNotFoundException;

    public abstract Drawable getActivityBanner(Intent var1) throws PackageManager.NameNotFoundException;

    public abstract Drawable getDefaultActivityIcon();

    public abstract Drawable getApplicationIcon(ApplicationInfo var1);

    public abstract Drawable getApplicationIcon(String var1) throws PackageManager.NameNotFoundException;

    public abstract Drawable getApplicationBanner(ApplicationInfo var1);

    public abstract Drawable getApplicationBanner(String var1) throws PackageManager.NameNotFoundException;

    public abstract Drawable getActivityLogo(ComponentName var1) throws PackageManager.NameNotFoundException;

    public abstract Drawable getActivityLogo(Intent var1) throws PackageManager.NameNotFoundException;

    public abstract Drawable getApplicationLogo(ApplicationInfo var1);

    public abstract Drawable getApplicationLogo(String var1) throws PackageManager.NameNotFoundException;

    public abstract Drawable getUserBadgedIcon(Drawable var1, UserHandle var2);

    public abstract Drawable getUserBadgedDrawableForDensity(Drawable var1, UserHandle var2, Rect var3, int var4);

    public abstract CharSequence getUserBadgedLabel(CharSequence var1, UserHandle var2);

    public abstract CharSequence getText(String var1, int var2, ApplicationInfo var3);

    public abstract XmlResourceParser getXml(String var1, int var2, ApplicationInfo var3);

    public abstract CharSequence getApplicationLabel(ApplicationInfo var1);

    public abstract Resources getResourcesForActivity(ComponentName var1) throws PackageManager.NameNotFoundException;

    public abstract Resources getResourcesForApplication(ApplicationInfo var1) throws PackageManager.NameNotFoundException;

    public abstract Resources getResourcesForApplication(String var1) throws PackageManager.NameNotFoundException;

    public PackageInfo getPackageArchiveInfo(String archiveFilePath, int flags) {
        throw new RuntimeException("Stub!");
    }

    public abstract void verifyPendingInstall(int var1, int var2);

    public abstract void extendVerificationTimeout(int var1, int var2, long var3);

    public abstract void setInstallerPackageName(String var1, String var2);

    public abstract String getInstallerPackageName(String var1);

    /** @deprecated */
    @Deprecated
    public abstract void addPackageToPreferred(String var1);

    /** @deprecated */
    @Deprecated
    public abstract void removePackageFromPreferred(String var1);

    public abstract List<PackageInfo> getPreferredPackages(int var1);

    /** @deprecated */
    @Deprecated
    public abstract void addPreferredActivity(IntentFilter var1, int var2, ComponentName[] var3, ComponentName var4);

    public abstract void clearPackagePreferredActivities(String var1);

    public abstract int getPreferredActivities(List<IntentFilter> var1, List<ComponentName> var2, String var3);

    public abstract void setComponentEnabledSetting(ComponentName var1, int var2, int var3);

    public abstract int getComponentEnabledSetting(ComponentName var1);

    public abstract void setApplicationEnabledSetting(String var1, int var2, int var3);

    public abstract int getApplicationEnabledSetting(String var1);

    public abstract boolean isSafeMode();

    public abstract PackageInstaller getPackageInstaller();

    public static class NameNotFoundException extends AndroidException {
        public NameNotFoundException() {
            throw new RuntimeException("Stub!");
        }

        public NameNotFoundException(String name) {
            throw new RuntimeException("Stub!");
        }
    }

    /** @hide */
    @IntDef({ PERMISSION_GRANTED, PERMISSION_DENIED })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionResult {
    }
}