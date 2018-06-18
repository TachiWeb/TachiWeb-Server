package xyz.nulldev.ts.api.v2.java.model.categories

import eu.kanade.tachiyomi.data.database.models.Manga

interface CategoryModel : CategoryLikeModel {
    override val id: Int

    override var name: String?

    override var order: Int?

    override var flags: Int?

    //TODO Use v2 manga structure
    override var manga: List<Manga>?
}