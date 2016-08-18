package xyz.nulldev.ts.sync.operation;

import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.conflict.Conflict;

import java.time.LocalDateTime;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public abstract class Operation {
    private LocalDateTime timestamp;

    public abstract String getName();

    /**
     * Try to apply an operation
     * @param library The library to apply the operation to
     * @return The conflict (null if no conflict)
     */
    public abstract Conflict tryApply(Library library);

    public abstract String toHumanForm();

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
