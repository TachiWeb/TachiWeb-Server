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

package xyz.nulldev.ts.api.http.manga;

import eu.kanade.tachiyomi.data.database.models.Manga;

import java.util.Arrays;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 29/07/16
 *
 * Manga flags (in a more modular interface).
 */
public enum MangaFlag {
    SORT_DIRECTION(Manga.SORT_MASK,
            new FlagState("DESCENDING", Manga.SORT_DESC),
            new FlagState("ASCENDING", Manga.SORT_ASC)),
    DISPLAY_MODE(Manga.DISPLAY_MASK,
            new FlagState("NAME", Manga.DISPLAY_NAME),
            new FlagState("NUMBER", Manga.DISPLAY_NUMBER)),
    READ_FILTER(Manga.READ_MASK,
            new FlagState("READ", Manga.SHOW_READ),
            new FlagState("UNREAD", Manga.SHOW_UNREAD),
            new FlagState("ALL", Manga.SHOW_ALL)),
    DOWNLOADED_FILTER(Manga.DOWNLOADED_MASK,
            new FlagState("DOWNLOADED", Manga.SHOW_DOWNLOADED),
            new FlagState("NOT_DOWNLOADED", Manga.SHOW_NOT_DOWNLOADED),
            new FlagState("ALL", Manga.SHOW_ALL)),
    SORT_TYPE(Manga.SORTING_MASK,
            new FlagState("SOURCE", Manga.SORTING_SOURCE),
            new FlagState("NUMBER", Manga.SORTING_NUMBER));

    private int flagMask;
    private FlagState[] flagStates;

    MangaFlag(int flagMask, FlagState... flagStates) {
        this.flagMask = flagMask;
        this.flagStates = flagStates;
    }

    public FlagState[] getFlagStates() {
        return flagStates;
    }

    public FlagState findFlagState(String name) {
        for (FlagState state : flagStates) {
            if (state.getName().equals(name)) {
                return state;
            }
        }
        return null;
    }

    public FlagState findFlagState(int value) {
        for (FlagState state : flagStates) {
            if (state.getValue() == value) {
                return state;
            }
        }
        return null;
    }

    public void set(Manga manga, FlagState state) {
        setFlag(manga, state.getValue(), flagMask);
    }

    private static void setFlag(Manga manga, int flag, int mask) {
        manga.setChapter_flags(manga.getChapter_flags() & ~mask | (flag & mask));
    }

    public FlagState get(Manga manga) {
        return findFlagState(manga.getChapter_flags() & flagMask);
    }

    @Override
    public String toString() {
        return "MangaFlag{" +
                "flagMask=" + flagMask +
                ", flagStates=" + Arrays.toString(flagStates) +
                '}';
    }

    public static class FlagState {
        private String name;
        private int value;

        FlagState(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FlagState flagState = (FlagState) o;

            return value == flagState.value
                    && (name != null ? name.equals(flagState.name) : flagState.name == null);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + value;
            return result;
        }

        @Override
        public String toString() {
            return "FlagState{" + "name='" + name + '\'' + ", value=" + value + '}';
        }
    }
}
