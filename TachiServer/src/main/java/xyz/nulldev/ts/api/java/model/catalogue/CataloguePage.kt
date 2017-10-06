package xyz.nulldev.ts.api.java.model.catalogue

import eu.kanade.tachiyomi.data.database.models.Manga

data class CataloguePage(val manga: List<Manga>,
                         val currentPage: Int,
                         val nextPage: Int?)
