package xyz.nulldev.ts.sync.json.operation;

import org.json.JSONObject;
import xyz.nulldev.ts.sync.json.node.JSONNode;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class RemovePropertySyncOperation extends SyncOperation {

    private final String property;

    public RemovePropertySyncOperation(JSONNode jsonNode, String property) {
        super(jsonNode);
        this.property = property;
    }

    @Override
    String getType() {
        return "REMOVE_PROPERTY";
    }

    @Override
    void apply(Object object) {
        JSONObject parentNode = (JSONObject) getJsonNode().getFromRoot(object);
        parentNode.remove(property);
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
}
