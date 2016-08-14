package xyz.nulldev.ts.sync.json.node;

import org.json.JSONArray;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class ArrayJSONNode extends JSONNode {

    private final int index;

    public ArrayJSONNode(int index) {
        this.index = index;
    }

    public ArrayJSONNode(JSONNode parentNode, int index) {
        super(parentNode);
        this.index = index;
    }

    @Override
    public boolean isValid(Object object) {
        return object instanceof JSONArray && ((JSONArray) object).length() > index;
    }

    @Override
    public Object get(Object object) {
        return ((JSONArray) object).get(index);
    }

    public int getIndex() {
        return index;
    }
}
