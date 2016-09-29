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
