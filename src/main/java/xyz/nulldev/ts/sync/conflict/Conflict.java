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
