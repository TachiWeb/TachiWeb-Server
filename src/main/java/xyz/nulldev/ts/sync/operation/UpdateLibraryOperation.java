package xyz.nulldev.ts.sync.operation;

import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.library.LibraryUpdater;
import xyz.nulldev.ts.sync.conflict.Conflict;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class UpdateLibraryOperation extends Operation {
    public static final String NAME = "Update Library";

    private LibraryUpdater updater;

    public UpdateLibraryOperation() {
        this.updater = new LibraryUpdater(DIReplacement.get().injectSourceManager());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Conflict tryApply(Library library) {
        updater.updateLibrary(library, true);
        return null;
    }

    @Override
    public String toHumanForm() {
        return "Update library.";
    }
}
