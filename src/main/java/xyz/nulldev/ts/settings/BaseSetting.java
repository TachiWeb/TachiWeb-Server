package xyz.nulldev.ts.settings;

import android.content.SharedPreferences;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 23/07/16
 *
 * Basic settings object
 */
public abstract class BaseSetting {
    private final String name;
    private final String description;
    private final String type;
    private Object value;

    public BaseSetting(String name, String description, String type, Object value) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Object getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public abstract void saveToSharedPrefs(SharedPreferences.Editor preferences, String key);

    public abstract void loadFromSharedPrefs(SharedPreferences preferences, String key);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseSetting that = (BaseSetting) o;

        return name != null
                ? name.equals(that.name)
                : that.name == null
                        && (description != null
                                ? description.equals(that.description)
                                : that.description == null
                                        && (type != null
                                                ? type.equals(that.type)
                                                : that.type == null
                                                        && (value != null
                                                                ? value.equals(that.value)
                                                                : that.value == null)));
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BaseSetting{"
                + "name='"
                + name
                + '\''
                + ", description='"
                + description
                + '\''
                + ", type='"
                + type
                + '\''
                + ", value="
                + value
                + '}';
    }
}
