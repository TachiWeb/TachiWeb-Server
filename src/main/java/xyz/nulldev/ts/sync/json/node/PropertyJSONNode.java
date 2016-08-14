package xyz.nulldev.ts.sync.json.node;

import org.json.JSONObject;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class PropertyJSONNode extends JSONNode {

    private final String key;

    public PropertyJSONNode(String key) {
        this.key = key;
    }

    public PropertyJSONNode(JSONNode parentNode, String key) {
        super(parentNode);
        this.key = key;
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof JSONObject && ((JSONObject) object).has(key);
    }

    @Override
    public Object get(Object object) {
        return ((JSONObject) object).get(key);
    }

    public String getKey() {
        return key;
    }
}
