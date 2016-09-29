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

package xyz.nulldev.ts.sync;

import eu.kanade.tachiyomi.data.database.models.Category;
import eu.kanade.tachiyomi.data.database.models.Chapter;
import eu.kanade.tachiyomi.data.database.models.Manga;
import xyz.nulldev.ts.library.Library;
import xyz.nulldev.ts.sync.operation.Operation;
import xyz.nulldev.ts.sync.operation.category.AddCategoryOperation;
import xyz.nulldev.ts.sync.operation.category.AddMangaToCategoryOperation;
import xyz.nulldev.ts.sync.operation.category.RemoveCategoryOperation;
import xyz.nulldev.ts.sync.operation.category.RemoveMangaFromCategoryOperation;
import xyz.nulldev.ts.sync.operation.chapter.ChangeChapterReadingStatusOperation;
import xyz.nulldev.ts.sync.operation.manga.*;
import xyz.nulldev.ts.util.OptionalUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: TachiServer
 * Author: nulldev
 * Creation Date: 14/08/16
 */
public class LibraryComparer {
    public static List<Operation> compareLibraries(Library oldLibrary, Library newLibrary) {
        List<Operation> mangaOperationList = new ArrayList<>();
        List<Operation> mangaUpdateOperationList = new ArrayList<>();
        List<Operation> chapterOperationList = new ArrayList<>();
        List<Operation> categoryOperationList = new ArrayList<>();
        //Compare categories (add and remove categories)
        List<Category> categoriesToRemove = new ArrayList<>(oldLibrary.getCategories());
        for (Category category : newLibrary.getCategories()) {
            Category oldCategory =
                    OptionalUtils.getOrNull(
                            oldLibrary
                                    .getCategories()
                                    .stream()
                                    .filter(
                                            oCategory ->
                                                    category.getName().equals(oCategory.getName()))
                                    .findFirst());
            //Old category does not exist
            if (oldCategory == null) {
                categoryOperationList.add(new AddCategoryOperation(category.getName()));
            } else {
                categoriesToRemove.remove(oldCategory);
            }
        }
        //Remove old categories
        for (Category notConsideredCategory : categoriesToRemove) {
            categoryOperationList.add(new RemoveCategoryOperation(notConsideredCategory.getName()));
        }

        List<Manga> deletedMangas = oldLibrary.getMangas();
        //Compare mangas
        for (Manga manga : newLibrary.getMangas()) {
            boolean shouldUpdate = false;
            //Check if manga in old library
            Manga mangaInOldLibrary = oldLibrary.getManga(manga.getUrl(), manga.getSource());
            //Manga is not in old library, add it
            if (mangaInOldLibrary == null) {
                mangaOperationList.add(
                        new AddMangaOperation(manga.getTitle(), manga.getUrl(), manga.getSource()));
                shouldUpdate = true;
            } else {
                deletedMangas.remove(mangaInOldLibrary);
            }
            //Compare manga contents now
            //Compare chapter flags
            if (mangaInOldLibrary == null
                    || manga.getChapter_flags() != mangaInOldLibrary.getChapter_flags()) {
                //Chapter flags do not match, sync them
                mangaOperationList.add(
                        new ChangeMangaChapterFlagsOperation(
                                manga.getTitle(),
                                manga.getUrl(),
                                manga.getSource(),
                                manga.getChapter_flags()));
            }
            //Compare favorite status
            if (mangaInOldLibrary == null
                    || manga.getFavorite() != mangaInOldLibrary.getFavorite()) {
                //Favorite status do not match, sync them
                mangaOperationList.add(
                        new ChangeMangaFavoriteStatusOperation(
                                manga.getTitle(),
                                manga.getUrl(),
                                manga.getSource(),
                                manga.getFavorite()));
            }
            //Compare viewer type
            if (mangaInOldLibrary == null || manga.getViewer() != mangaInOldLibrary.getViewer()) {
                //Viewer type do not match, sync them
                mangaOperationList.add(
                        new ChangeMangaViewerOperation(
                                manga.getTitle(),
                                manga.getUrl(),
                                manga.getSource(),
                                manga.getViewer()));
            }
            //Compare chapters
            List<Chapter> chaptersInOldLibrary = mangaInOldLibrary != null ? oldLibrary.getChapters(mangaInOldLibrary) : Collections.emptyList();
            for (Chapter chapter : newLibrary.getChapters(manga)) {
                Chapter matchingOldChapter =
                        OptionalUtils.getOrNull(
                                chaptersInOldLibrary
                                        .stream()
                                        .filter(
                                                oldChapter ->
                                                        oldChapter.getUrl().equals(chapter.getUrl()))
                                        .findFirst());
                //Set reading status if old chapter does not exist or reading status different
                if (matchingOldChapter == null
                        || chapter.getRead() != matchingOldChapter.getRead()
                        || chapter.getLast_page_read() != matchingOldChapter.getLast_page_read()) {
                    chapterOperationList.add(
                            new ChangeChapterReadingStatusOperation(
                                    manga.getTitle(),
                                    manga.getUrl(),
                                    manga.getSource(),
                                    chapter.getChapter_number(),
                                    chapter.getRead(),
                                    chapter.getLast_page_read()));
                    shouldUpdate = true;
                }
            }
            //Compare categories (add and remove manga from categories)
            List<Category> newCategories = newLibrary.getCategoriesForManga(manga);
            List<Category> oldCategories;
            if (mangaInOldLibrary != null) {
                oldCategories = oldLibrary.getCategoriesForManga(mangaInOldLibrary);
            } else {
                oldCategories = new ArrayList<>();
            }
            List<Category> notConsideredCategories = new ArrayList<>(oldCategories);
            for (Category category : newCategories) {
                Category oldCategory =
                        OptionalUtils.getOrNull(
                                oldCategories
                                        .stream()
                                        .filter(
                                                oCategory ->
                                                        category.getName()
                                                                .equals(oCategory.getName()))
                                        .findFirst());
                if (oldCategory == null) {
                    //Category missing
                    categoryOperationList.add(
                            new AddMangaToCategoryOperation(
                                    manga.getTitle(),
                                    manga.getUrl(),
                                    manga.getSource(),
                                    category.getName()));
                } else {
                    notConsideredCategories.remove(oldCategory);
                }
            }
            //Delete categories not in new library
            for (Category notConsideredCategory : notConsideredCategories) {
                categoryOperationList.add(
                        new RemoveMangaFromCategoryOperation(
                                manga.getTitle(),
                                manga.getUrl(),
                                manga.getSource(),
                                notConsideredCategory.getName()));
            }
            //Update the manga if we are modifying it's chapter statuses or if it is new
            if(shouldUpdate) {
                mangaUpdateOperationList.add(new UpdateMangaOperation(manga.getTitle(), manga.getUrl(), manga.getSource()));
            }
        }
        //Unfavorite deleted mangas
        for(Manga manga : deletedMangas) {
            mangaOperationList.add(new ChangeMangaFavoriteStatusOperation(
                    manga.getTitle(),
                    manga.getUrl(),
                    manga.getSource(),
                    false));
        }

        //Generate final operation
        List<Operation> operationList = new ArrayList<>();
        operationList.addAll(mangaOperationList);
        operationList.addAll(mangaUpdateOperationList);
        operationList.addAll(chapterOperationList);
        operationList.addAll(categoryOperationList);
        return operationList;
    }
}
