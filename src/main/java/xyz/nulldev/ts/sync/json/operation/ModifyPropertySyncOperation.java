package xyz.nulldev.ts.sync.json.operation;

import org.json.JSONObject;
import xyz.nulldev.ts.sync.json.node.JSONNode;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class ModifyPropertySyncOperation extends SyncOperation {

    private final String property;
    private final Object newValue;

    public ModifyPropertySyncOperation(JSONNode jsonNode, String property, Object newValue) {
        super(jsonNode);
        this.property = property;
        this.newValue = newValue;
    }

    @Override
    String getType() {
        return "MODIFY_PROPERTY";
    }

    @Override
    void apply(Object object) {
        JSONObject parentNode = (JSONObject) getJsonNode().getFromRoot(object);
        parentNode.put(property, newValue);
    }

    @Override
    boolean canBeApplied(Object object) {
        Object parentNode = null;
        try {
            parentNode = getJsonNode().getFromRoot(object);
        } catch (Exception ignored) {}
        return parentNode != null && parentNode instanceof JSONObject;
    }

    public String getProperty() {
        return property;
    }

    public Object getNewValue() {
        return newValue;
    }
}
