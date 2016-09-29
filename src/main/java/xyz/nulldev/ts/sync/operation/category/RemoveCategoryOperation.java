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

package xyz.nulldev.ts.sync.operation.category;

import eu.kanade.tachiyomi.data.database.models.Category;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;
import xyz.nulldev.ts.sync.operation.Operation;

import java.util.Iterator;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class RemoveCategoryOperation extends Operation {
    public static final String NAME = "Remove Category";

    private final String oldCategoryName;

    public RemoveCategoryOperation(String oldCategoryName) {
        this.oldCategoryName = oldCategoryName;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Conflict tryApply(Library library) {
        Iterator<Category> categoryIterator = library.getCategories().iterator();
        while(categoryIterator.hasNext()) {
            if(categoryIterator.next().getName().equals(oldCategoryName)) {
                categoryIterator.remove();
                return null;
            }
        }
        return null;
    }

    @Override
    public String toHumanForm() {
        return "Remove category: " + oldCategoryName + ".";
    }
}
