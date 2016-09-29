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
