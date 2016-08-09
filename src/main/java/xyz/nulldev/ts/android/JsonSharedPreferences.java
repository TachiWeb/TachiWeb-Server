package xyz.nulldev.ts.android;

import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 10/07/16
 */

/**
 * JSON implementation of Android's SharedPreferences.
 */
public class JsonSharedPreferences implements SharedPreferences {

    private static final String KEY_TYPE = "t";
    private static final String KEY_VALUE = "v";

    private Map<String, Object> prefs = new HashMap<>(); //In-memory preference values
    private List<OnSharedPreferenceChangeListener> listeners = new ArrayList<>(); //Change listeners
    private File file; //Where the values should be stored

    public JsonSharedPreferences(File file) {
        this.file = file;
        //Load previous values if they exist
        if(file != null) {
            if(file.exists()) {
                try {
                    loadFromString(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
                } catch (IOException ignored) {
                    //TODO Logging
                    System.out.println("Failed to read shared prefs from String!");
                    ignored.printStackTrace();
                }
            }
        }
    }

    public synchronized void loadFromString(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            //Changes are need to be applied atomically so we temporarily store all changes in a seperate map
            Map<String, Object> tempMap = new HashMap<>();
            //Loop through all preference objects in JSON
            for (String key : jsonObject.keySet()) {
                JSONObject object = jsonObject.getJSONObject(key);
                //Load the object's Java type
                String typeString = object.getString(KEY_TYPE);
                PrefType type = PrefType.valueOf(typeString);
                Object res;
                //Map the JSON object's value to it's Java value
                switch (type) {
                    case String:
                        res = object.getString(KEY_VALUE);
                        break;
                    case StringSet:
                        Set<String> set = new HashSet<>();
                        JSONArray array = object.getJSONArray(KEY_VALUE);
                        for (int i = 0; i < array.length(); i++) {
                            set.add(array.getString(i));
                        }
                        res = set;
                        break;
                    case Int:
                        res = object.getInt(KEY_VALUE);
                        break;
                    case Long:
                        res = object.getLong(KEY_VALUE);
                        break;
                    case Float:
                        res = Float.parseFloat(object.getString(KEY_VALUE));
                        break;
                    case Boolean:
                        res = object.getBoolean(KEY_VALUE);
                        break;
                    default:
                    case Null:
                        res = null;
                        break;
                }
                //Queue the loaded object for placement into the in-memory preference map
                tempMap.put(key, res);
            }
            //Apply all changes made to the in-memory preference map atomically
            prefs = tempMap;
        } catch (JSONException e) {
            throw new RuntimeException("Error parsing JSON shared preferences!");
        }
    }

    public synchronized String saveToString() {
        JSONObject object = new JSONObject();
        //Loop through every preference in the in-memory preference map
        for (Map.Entry<String, Object> entry : prefs.entrySet()) {
            JSONObject entryObj = new JSONObject();
            Object value = entry.getValue();
            //Determine the object's type
            PrefType type = PrefType.fromObject(value);
            if (type == PrefType.Float) {
                value = value.toString();
            }
            //Put the preference's type and value into a JSON object
            entryObj.put(KEY_TYPE, type.name());
            entryObj.put(KEY_VALUE, value);
            object.put(entry.getKey(), entryObj);
        }
        return object.toString();
    }

    @Override
    public synchronized Map<String, ?> getAll() {
        Map<String, Object> clone = new HashMap<>();
        clone.putAll(prefs);
        return clone;
    }

    private <T> T fallbackIfNull(T obj, T fallback) {
        if (obj == null) {
            return fallback;
        }
        return obj;
    }

    @Override
    public synchronized String getString(String s, String s1) {
        return fallbackIfNull((String) prefs.get(s), s1);
    }

    @Override
    public synchronized Set<String> getStringSet(String s, Set<String> set) {
        return fallbackIfNull((Set<String>) prefs.get(s), set);
    }

    @Override
    public synchronized int getInt(String s, int i) {
        return fallbackIfNull((Integer) prefs.get(s), i);
    }

    @Override
    public synchronized long getLong(String s, long l) {
        return fallbackIfNull((Long) prefs.get(s), l);
    }

    @Override
    public synchronized float getFloat(String s, float v) {
        return fallbackIfNull((Float) prefs.get(s), v);
    }

    @Override
    public synchronized boolean getBoolean(String s, boolean b) {
        return fallbackIfNull((Boolean) prefs.get(s), b);
    }

    @Override
    public synchronized boolean contains(String s) {
        return prefs.containsKey(s);
    }

    @Override
    public Editor edit() {
        return new JsonSharedPreferencesEditor();
    }

    @Override
    public synchronized void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        if (listeners.contains(onSharedPreferenceChangeListener)) {
            listeners.add(onSharedPreferenceChangeListener);
        }
    }

    @Override
    public synchronized void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        listeners.remove(onSharedPreferenceChangeListener);
    }

    private class JsonSharedPreferencesEditor implements Editor {

        Map<String, Object> prefsClone = new HashMap<>(prefs);

        List<String> affectedKeys = new ArrayList<>(); //List of all affected keys to invoke listeners on once changes applied

        private void recordChange(String key) {
            if (!affectedKeys.contains(key)) {
                affectedKeys.add(key);
            }
        }

        @Override
        public synchronized Editor putString(String s, String s1) {
            prefsClone.put(s, s1);
            recordChange(s);
            return this;
        }

        @Override
        public synchronized Editor putStringSet(String s, Set<String> set) {
            Set<String> clonedSet = new HashSet<>();
            clonedSet.addAll(set);
            prefsClone.put(s, clonedSet);
            recordChange(s);
            return this;
        }

        @Override
        public synchronized Editor putInt(String s, int i) {
            prefsClone.put(s, i);
            recordChange(s);
            return this;
        }

        @Override
        public synchronized Editor putLong(String s, long l) {
            prefsClone.put(s, l);
            recordChange(s);
            return this;
        }

        @Override
        public synchronized Editor putFloat(String s, float v) {
            prefsClone.put(s, v);
            recordChange(s);
            return this;
        }

        @Override
        public synchronized Editor putBoolean(String s, boolean b) {
            prefsClone.put(s, b);
            recordChange(s);
            return this;
        }

        @Override
        public synchronized Editor remove(String s) {
            prefsClone.remove(s);
            recordChange(s);
            return this;
        }

        @Override
        public synchronized Editor clear() {
            prefsClone.keySet().forEach(this::recordChange);
            prefsClone.clear();
            return this;
        }

        @Override
        public synchronized boolean commit() {
            synchronized (JsonSharedPreferences.this) {
                //Backup prefs
                Map<String, Object> oldPrefs = prefs;
                prefs = prefsClone;
                if(file != null) {
                    //Delete old on-disk copy of preferences
                    if(file.exists()) {
                        file.delete();
                    }
                    //Save new preferences to disk
                    String string = saveToString();
                    try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)))) {
                        writer.print(string);
                    } catch (IOException e) {
                        //TODO Logging
                        System.out.println("Failed to save shared prefs!");
                        e.printStackTrace();
                        prefs = oldPrefs;
                        return false;
                    }
                }
                //Invoke preference change listeners
                for (String key : affectedKeys) {
                    for (OnSharedPreferenceChangeListener listener : listeners) {
                        listener.onSharedPreferenceChanged(JsonSharedPreferences.this, key);
                    }
                }
                affectedKeys.clear();
                return true;
            }
        }

        @Override
        public void apply() {
            //TODO Threading?
            commit();
        }
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    /** Preference types (for storing what type of object a preference is)**/
    private enum PrefType {
        String,
        StringSet,
        Int,
        Long,
        Float,
        Boolean,
        Null;

        public static PrefType fromObject(Object object) {
            if (object == null) {
                return Null;
            }
            if (object instanceof String) {
                return String;
            } else if (object instanceof Set || Set.class.isAssignableFrom(object.getClass())) {
                return StringSet;
            } else if (object instanceof Integer) {
                return Int;
            } else if (object instanceof Long) {
                return Long;
            } else if (object instanceof Float) {
                return Float;
            } else if (object instanceof Boolean) {
                return Boolean;
            }
            throw new IllegalArgumentException("Could not find type of object: " + object);
        }
    }
}
