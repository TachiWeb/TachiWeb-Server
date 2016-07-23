package xyz.nulldev.ts.settings;

import android.content.SharedPreferences;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 23/07/16
 */
public class BooleanSetting extends BaseSetting {
    public BooleanSetting(String name, String description, String type, Object value) {
        super(name, description, type, value);
    }

    @Override
    public Boolean getValue() {
        return (Boolean) super.getValue();
    }

    @Override
    public void setValue(Object value) {
        if(value != null && !(value instanceof Boolean)) {
            throw new IllegalArgumentException("Value is not a Boolean!");
        }
        super.setValue(value);
    }

    @Override
    public void saveToSharedPrefs(SharedPreferences.Editor preferences, String key) {
        preferences.putBoolean(key, getValue());
    }

    @Override
    public void loadFromSharedPrefs(SharedPreferences preferences, String key) {
        setValue(preferences.getBoolean(key, getValue()));
    }
}
