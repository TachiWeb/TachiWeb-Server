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
