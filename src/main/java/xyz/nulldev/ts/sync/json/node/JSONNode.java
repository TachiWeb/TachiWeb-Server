/*
 * Copyright 2016 Andy Bao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
