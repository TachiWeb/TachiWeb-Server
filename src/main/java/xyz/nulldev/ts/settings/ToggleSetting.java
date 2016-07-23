package xyz.nulldev.ts.settings;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 23/07/16
 */
public class ToggleSetting extends BooleanSetting {
    public static final String TYPE = "TOGGLE";

    public ToggleSetting(String name, String description, Object value) {
        super(name, description, TYPE, value);
    }
}
