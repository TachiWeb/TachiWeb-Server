package eu.kanade.tachiyomi.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import eu.kanade.tachiyomi.data.source.online.LoginSource;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 11/07/16
 */
public class PreferencesHelper {
    private Context context;

    public PreferencesHelper(Context context) {
        this.context = context;
        loginPrefs = context.getSharedPreferences("lsource_creds", Context.MODE_PRIVATE);
    }

    public boolean reencodeImage() {
        return false;
    }

    private SharedPreferences loginPrefs;

    public String getSourceUsernameKey(LoginSource source) {
        return source.getId() + "_username";
    }

    public String getSourcePasswordKey(LoginSource source) {
        return source.getId() + "_password";
    }

    public void storeLoginCreds(LoginSource source, String username, String password) {
        loginPrefs.edit().putString(getSourceUsernameKey(source), username).putString(getSourcePasswordKey(source), password).commit();
    }

    public String sourceUsername(LoginSource source) {
        return loginPrefs.getString(getSourceUsernameKey(source), "");
    }

    public String sourcePassword(LoginSource source) {
        return loginPrefs.getString(getSourcePasswordKey(source), "");
    }
}
