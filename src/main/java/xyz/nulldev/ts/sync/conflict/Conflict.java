package xyz.nulldev.ts.sync.conflict;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class Conflict {
    private final String description;
    private final Resolution resolution;

    public Conflict(String description) {
        this.description = description;
        this.resolution = null;
    }

    public Conflict(String description, Resolution resolution) {
        this.description = description;
        this.resolution = resolution;
    }

    public String getDescription() {
        return description;
    }

    public Resolution getResolution() {
        return resolution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Conflict conflict = (Conflict) o;

        return description != null
                ? description.equals(conflict.description)
                : conflict.description == null
                        && (resolution != null
                                ? resolution.equals(conflict.resolution)
                                : conflict.resolution == null);
    }

    @Override
    public int hashCode() {
        int result = description != null ? description.hashCode() : 0;
        result = 31 * result + (resolution != null ? resolution.hashCode() : 0);
        return result;
    }
}
