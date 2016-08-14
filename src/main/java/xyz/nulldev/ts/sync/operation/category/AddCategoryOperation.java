package xyz.nulldev.ts.sync.operation.category;

import eu.kanade.tachiyomi.data.database.models.Category;
import xyz.nulldev.ts.Library;
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
