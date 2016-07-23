package xyz.nulldev.ts.settings;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 23/07/16
 */
public class CheckboxSetting extends BooleanSetting {
    public static final String TYPE = "CHECKBOX";

    public CheckboxSetting(String name, String description, Object value) {
        super(name, description, TYPE, value);
    }
}
