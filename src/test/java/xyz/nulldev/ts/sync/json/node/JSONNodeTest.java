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

import junit.framework.TestCase;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
@Deprecated
public class JSONNodeTest extends TestCase {
    public void testGetFromRoot() throws Exception {
        JSONObject rootObject = new JSONObject();
        JSONObject thisObject = new JSONObject();
        rootObject.put("this", thisObject);
        JSONObject isObject = new JSONObject();
        thisObject.put("is", isObject);
        JSONObject aObject = new JSONObject();
        isObject.put("a", aObject);
        JSONObject testObject = new JSONObject();
        aObject.put("test", testObject);
        JSONArray arrayObject = new JSONArray();
        testObject.put("array", arrayObject);
        JSONObject firstIndexObject = new JSONObject();
        arrayObject.put(firstIndexObject);
        JSONObject secondIndexObject = new JSONObject();
        arrayObject.put(secondIndexObject);
        JSONObject doneObject = new JSONObject();
        secondIndexObject.put("done", doneObject);

        JSONNode thisNode = new PropertyJSONNode("this");
        JSONNode isNode = new PropertyJSONNode(thisNode, "is");
        JSONNode aNode = new PropertyJSONNode(isNode, "a");
        JSONNode testNode = new PropertyJSONNode(aNode, "test");
        JSONNode arrayNode = new PropertyJSONNode(testNode, "array");
        JSONNode secondIndexNode = new ArrayJSONNode(arrayNode, 1);
        JSONNode doneNode = new PropertyJSONNode(secondIndexNode, "done");

        assertEquals(thisObject, thisNode.getFromRoot(rootObject));
        assertEquals(isObject, isNode.getFromRoot(rootObject));
        assertEquals(aObject, aNode.getFromRoot(rootObject));
        assertEquals(testObject, testNode.getFromRoot(rootObject));
        assertEquals(secondIndexObject, secondIndexNode.getFromRoot(rootObject));
        assertEquals(doneObject, doneNode.getFromRoot(rootObject));
    }
}