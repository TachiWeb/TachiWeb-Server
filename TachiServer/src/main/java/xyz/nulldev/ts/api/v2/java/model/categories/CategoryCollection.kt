package xyz.nulldev.ts.api.v2.java.model.categories

import eu.kanade.tachiyomi.data.database.models.Manga

interface CategoryCollection : CategoryLikeModel, List<CategoryModel> {
    override val id: List<Int>

    override var name: List<String?>

    override var order: List<Int?>

    override var flags: List<Int?>

    //TODO Use v2 manga structure
    override var manga: List<List<Manga>?>
}