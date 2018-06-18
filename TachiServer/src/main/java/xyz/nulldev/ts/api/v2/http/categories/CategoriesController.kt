package xyz.nulldev.ts.api.v2.http.categories

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.http.Response
import xyz.nulldev.ts.api.v2.http.jvcompat.*
import xyz.nulldev.ts.api.v2.java.model.categories.CategoryCollection
import xyz.nulldev.ts.ext.kInstanceLazy

object CategoriesController : BaseController() {
    private val db: DatabaseHelper by kInstanceLazy()

    //TODO Swap to Javalin attribute passing
    fun prepareCategoriesAttributes(ctx: Context) {
        val categoriesParam = ctx.param(CATEGORIES_PARAM)

        ctx.attribute(CATEGORIES_ATTR, if(categoriesParam != null)
            api.categories.get(*categoriesParam.split(",").map {
                it.trim().toInt()
            }.toIntArray())
        else
            api.categories.getAll()
        )
    }

    private val CATEGORIES_PARAM = ":categories"
    private val CATEGORIES_ATTR = "categories"

    fun getName(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        getApiField(ctx,
                CATEGORIES_ATTR,
                CategoryCollection::id,
                CategoryCollection::name,
                CategoryName::class)
    }

    fun setName(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        setApiField(ctx,
                CATEGORIES_ATTR,
                CategoryCollection::id,
                CategoryCollection::name,
                CategoryName::id,
                CategoryName::name)
    }

    fun getOrder(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        getApiField(ctx,
                CATEGORIES_ATTR,
                CategoryCollection::id,
                CategoryCollection::order,
                CategoryOrder::class)
    }

    fun setOrder(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        setApiField(ctx,
                CATEGORIES_ATTR,
                CategoryCollection::id,
                CategoryCollection::order,
                CategoryOrder::id,
                CategoryOrder::order)
    }

    fun getFlags(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        getApiField(ctx,
                CATEGORIES_ATTR,
                CategoryCollection::id,
                CategoryCollection::flags,
                CategoryFlags::class)
    }

    fun setFlags(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        setApiField(ctx,
                CATEGORIES_ATTR,
                CategoryCollection::id,
                CategoryCollection::flags,
                CategoryFlags::id,
                CategoryFlags::flags)
    }

    fun getCategory(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        ctx.json(Response.Success(ctx.attribute<CategoryCollection>(CATEGORIES_ATTR).map {
            SerializableCategoryModel(it)
        }))
    }

    fun addCategory(ctx: Context) {
        val template = ctx.bodyAsClass<CategoryTemplate>()

        val res = SerializableCategoryModel(api.categories.add(template.name,
                template.order,
                template.flags ?: 0))

        ctx.json(Response.Success(res))
    }

    fun deleteCategory(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        ctx.attribute<CategoryCollection>(CATEGORIES_ATTR).delete()

        ctx.json(Response.Success())
    }

    fun getManga(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        val attr = ctx.attribute<CategoryCollection>(CATEGORIES_ATTR)

        ctx.json(Response.Success(attr.manga.mapIndexed { index, data ->
            CategoryManga(attr.id[index], data?.map { it.id!! })
        }))
    }

    fun setManga(ctx: Context) {
        prepareCategoriesAttributes(ctx)

        val attr = ctx.attribute<CategoryCollection>(CATEGORIES_ATTR)

        val new = ctx.bodyAsClass<Array<CategoryManga>>().toList()

        attr.manga = attr.id.map { item ->
            new.find { it.id == item }?.let {
                it.manga?.map { mangaId ->
                    db.getManga(mangaId).executeAsBlocking()
                        ?: error("Manga $mangaId does not exist!")
                }
            }
        }

        ctx.json(Response.Success())
    }
}
