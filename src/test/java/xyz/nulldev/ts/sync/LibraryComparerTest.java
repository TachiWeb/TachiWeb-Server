package xyz.nulldev.ts.sync;

import junit.framework.TestCase;
import xyz.nulldev.ts.DIReplacement;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;
import xyz.nulldev.ts.sync.operation.Operation;

import java.io.File;
import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class LibraryComparerTest extends TestCase {
    public void testCompareLibraries() throws Exception {
        Library mainLibrary = DIReplacement.get().getLibrary();
        DIReplacement.get().injectBackupManager().restoreFromFile(new File("/home/nulldev/Documents/Dev/Intellij Projects/TachiServer/data/library/library.json"), mainLibrary);
        List<Operation> operationList = LibraryComparer.compareLibraries(new Library(), DIReplacement.get().getLibrary());
        for(Operation operation : operationList) {
            System.out.println(operation.toHumanForm());
        }
        System.out.println("=========[Applying changes]=========");
        Library emptyLibrary = new Library();
        for(Operation operation : operationList) {
            Conflict conflict = operation.tryApply(emptyLibrary);
            if(conflict != null) {
                System.out.println("CONFLICTS: " + conflict.getDescription());
            }
        }
        System.out.println(emptyLibrary);
    }
}