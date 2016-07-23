package xyz.nulldev.ts.settings;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import static xyz.nulldev.ts.settings.Settings.SETTINGS_JOINER;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 23/07/16
 *
 * A settings group holding nested settings
 */
public class SettingsGroup extends BaseSetting {
    public static final String TYPE = "GROUP";

    private final List<BaseSetting> subSettings = new ArrayList<>();

    public SettingsGroup(String name, String description) {
        super(name, description, TYPE, null);
    }

    public List<BaseSetting> getSubSettings() {
        return subSettings;
    }

    public void registerSubSetting(BaseSetting baseSetting) {
        subSettings.add(baseSetting);
    }

    private String buildEntryKey(String key, BaseSetting setting) {
        return key + SETTINGS_JOINER + setting.getName();
    }

    @Override
    public void saveToSharedPrefs(SharedPreferences.Editor preferences, String key) {
        for(BaseSetting entry : subSettings) {
            entry.saveToSharedPrefs(preferences, buildEntryKey(key, entry));
        }
    }

    @Override
    public void loadFromSharedPrefs(SharedPreferences preferences, String key) {
        for(BaseSetting entry : subSettings) {
            entry.loadFromSharedPrefs(preferences, buildEntryKey(key, entry));
        }
    }
}
