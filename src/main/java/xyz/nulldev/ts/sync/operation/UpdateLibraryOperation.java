package xyz.nulldev.ts.sync.operation;

import xyz.nulldev.ts.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class UpdateLibraryOperation extends Operation {
    public static final String NAME = "Update Library";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Conflict tryApply(Library library) {
        //TODO Server side library update code
        return null;
    }

    @Override
    public String toHumanForm() {
        return "Update library.";
    }
}
