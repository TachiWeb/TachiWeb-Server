package xyz.nulldev.ts.api.v2.java.impl.categories

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.database.models.MangaCategory
import xyz.nulldev.ts.api.v2.java.impl.util.DbMapper
import xyz.nulldev.ts.api.v2.java.impl.util.ProxyList
import xyz.nulldev.ts.api.v2.java.model.categories.CategoryCollection
import xyz.nulldev.ts.api.v2.java.model.categories.CategoryModel
import xyz.nulldev.ts.ext.kInstanceLazy

class CategoryCollectionImpl(override val id: List<Int>) : CategoryCollection,
        List<CategoryModel> by ProxyList(id, { CategoryCollectionProxy(it) }) {
    private val db: DatabaseHelper by kInstanceLazy()

    private val dbMapper = DbMapper(
            id,
            dbGetter = { db.getCategory(it).executeAsBlocking() },
            dbSetter = { db.insertCategory(it).executeAsBlocking() }
    )

    // TODO Prevent renaming to same name as another category
    override var name: List<String?>
        get() = dbMapper.mapGet {
            it.name
        }
        set(value) = dbMapper.mapSet(value) { category, name ->
            category.name = name
        }

    override var order: List<Int?>
        get() = dbMapper.mapGet {
            it.order
        }
        set(value) = dbMapper.mapSet(value) { category, order ->
            category.order = order
        }

    override var flags: List<Int?>
        get() = dbMapper.mapGet {
            it.flags
        }
        set(value) = dbMapper.mapSet(value) { category, flags ->
            category.flags = flags
        }

    override var manga: List<List<Manga>?>
        get() = dbMapper.mapGet {
            db.getMangaCategoriesForCategory(it).executeAsBlocking().mapNotNull {
                db.getManga(it.manga_id).executeAsBlocking()
            }
        }
        set(value) {
            db.inTransaction {
                id.forEachIndexed { index, i ->
                    val new = value[index] ?: return@forEachIndexed

                    val category = dbMapper.dbGetter(i)
                    checkNotNull(category) { "Object with id $i no longer exists!" }

                    val nowMcs = new.map { MangaCategory.create(it, category!!) }

                    val mcs = db.getMangaCategoriesForCategory(category!!).executeAsBlocking()
                    val deletedMcs = mcs.toMutableList().apply {
                        // Old - new = deleted
                        removeAll(nowMcs)
                    }

                    val newMcs = nowMcs.toMutableList().apply {
                        // New - old = new
                        removeAll(mcs)
                    }

                    deletedMcs.forEach { db.deleteMangaCategory(it).executeAsBlocking() }
                    db.insertMangasCategories(newMcs).executeAsBlocking()
                }
            }
        }

    override fun delete() = dbMapper.mapDelete {
        db.deleteCategory(it).executeAsBlocking()
    }
}

class CategoryCollectionProxy(override val id: Int) : CategoryModel {
    private val collection = CategoryCollectionImpl(listOf(id))

    override var name: String?
        get() = collection.name[0]
        set(value) { collection.name = listOf(value) }

    override var order: Int?
        get() = collection.order[0]
        set(value) { collection.order = listOf(value) }

    override var flags: Int?
        get() = collection.flags[0]
        set(value) { collection.flags = listOf(value) }

    override var manga: List<Manga>?
        get() = collection.manga[0]
        set(value) { collection.manga = listOf(value) }

    override fun delete() = collection.delete()
}
