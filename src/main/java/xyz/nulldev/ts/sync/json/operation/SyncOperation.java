package xyz.nulldev.ts.sync.json.operation;

import xyz.nulldev.ts.sync.json.node.JSONNode;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public abstract class SyncOperation {

    private final JSONNode jsonNode;

    public SyncOperation(JSONNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    abstract String getType();
    abstract void apply(Object object);
    abstract boolean canBeApplied(Object object);

    public JSONNode getJsonNode() {
        return jsonNode;
    }
}
