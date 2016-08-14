package xyz.nulldev.ts.sync.json.node;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public abstract class JSONNode {

    private JSONNode parentNode = null;

    public JSONNode() {
    }

    public JSONNode(JSONNode parentNode) {
        this.parentNode = parentNode;
    }

    abstract boolean isValid(Object object);
    abstract Object get(Object object);

    public Object getFromRoot(Object root) {
        if(parentNode == null) {
            return get(root);
        }
        return get(parentNode.getFromRoot(root));
    }

    public JSONNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(JSONNode parentNode) {
        this.parentNode = parentNode;
    }
}
