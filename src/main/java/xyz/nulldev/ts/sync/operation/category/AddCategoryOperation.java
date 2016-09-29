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

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class AddCategoryOperation extends Operation {
    public static final String NAME = "Add Category";

    private final String newCategoryName;

    public AddCategoryOperation(String newCategoryName) {
        this.newCategoryName = newCategoryName;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Conflict tryApply(Library library) {
        for(Category category : library.getCategories()) {
            if(category.getName().equals(newCategoryName)) {
                return null;
            }
        }
        Category category = Category.Companion.create(newCategoryName);
        int id = library.insertCategory(category);
        category.setId(id);
        return null;
    }

    @Override
    public String toHumanForm() {
        return "Add category: " + newCategoryName + ".";
    }
}
