package xyz.nulldev.ts.api.v3.operations.categories

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Category
import eu.kanade.tachiyomi.data.database.models.MangaCategory
import io.vertx.core.Vertx
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import xyz.nulldev.ts.api.v3.OperationGroup
import xyz.nulldev.ts.api.v3.models.categories.WCategory
import xyz.nulldev.ts.api.v3.models.categories.WMutateCategoryMangaRequest
import xyz.nulldev.ts.api.v3.models.categories.WMutateCategoryRequest
import xyz.nulldev.ts.api.v3.models.exceptions.WErrorTypes
import xyz.nulldev.ts.api.v3.models.exceptions.WErrorTypes.*
import xyz.nulldev.ts.api.v3.op
import xyz.nulldev.ts.api.v3.opWithParams
import xyz.nulldev.ts.api.v3.util.await
import xyz.nulldev.ts.ext.kInstanceLazy

private const val CATEGORY_ID_PARAM = "categoryId"

class CategoryOperations(private val vertx: Vertx) : OperationGroup {
    private val db: DatabaseHelper by kInstanceLazy()

    override fun register(routerFactory: OpenAPI3RouterFactory) {
        routerFactory.op(::getCategories.name, ::getCategories)
        routerFactory.op(::createCategory.name, ::createCategory)
        routerFactory.opWithParams(::getCategory.name, CATEGORY_ID_PARAM, ::getCategory)
        routerFactory.opWithParams(::editCategory.name, CATEGORY_ID_PARAM, ::editCategory)
        routerFactory.opWithParams(::deleteCategory.name, CATEGORY_ID_PARAM, ::deleteCategory)
        routerFactory.opWithParams(::editCategoryManga.name, CATEGORY_ID_PARAM, ::editCategoryManga)
    }

    suspend fun getCategories(): List<WCategory> {
        val favoriteManga = db.getFavoriteMangas().await()
        val realCategories = db.getCategories().await().map {
            it.asWeb()
        }
        val noCategoryManga = favoriteManga.filter { manga ->
            realCategories.none { category -> manga.id in category.manga }
        }
        return listOf(
                WCategory(
                        -1,
                        noCategoryManga.map { it.id!! },
                        DEFAULT_CATEGORY_NAME,
                        -1
                )
        ) + realCategories
    }

    suspend fun createCategory(request: WMutateCategoryRequest): WCategory {
        val existingCategories = db.getCategories().await()
        validateMutationRequest(existingCategories, request)

        val newCategory = Category.create(request.name).apply {
            order = request.order ?: ((existingCategories.maxBy { it.order }?.order ?: 0) + 1)
        }
        newCategory.id = db.insertCategory(newCategory).await().insertedId()!!.toInt()

        return newCategory.asWeb()
    }

    suspend fun getCategory(categoryId: Int): WCategory {
        return db.getCategory(categoryId).await()?.asWeb() ?: notFound()
    }

    suspend fun editCategory(categoryId: Int, request: WMutateCategoryRequest): WCategory {
        val existingCategoriesWithoutSelf = db.getCategories().await().filter {
            it.id != categoryId
        }
        validateMutationRequest(existingCategoriesWithoutSelf, request)

        val category = db.getCategory(categoryId).await()?.apply {
            name = request.name
            order = request.order ?: existingCategoriesWithoutSelf.maxBy { it.order }?.order ?: 1
        } ?: notFound()
        db.insertCategory(category).await()
        return category.asWeb()
    }

    suspend fun deleteCategory(categoryId: Int) {
        db.deleteCategory(db.getCategory(categoryId).await() ?: notFound()).await()
    }

    suspend fun editCategoryManga(categoryId: Int, request: WMutateCategoryMangaRequest): Array<Long> {
        val category = db.getCategory(categoryId).await() ?: notFound(WErrorTypes.NO_CATEGORY)
        if (request.add.any { it in request.remove }) badRequest()

        val allMangas = db.getMangas().await().sortedBy { it.id }
        val mangaToAdd = allMangas.filter { it.id in request.add }
        val mangaToRemove = allMangas.filter { it.id in request.remove }
        if (mangaToAdd.size != request.add.size || mangaToRemove.size != request.remove.size) notFound(NO_MANGA)

        val mangaCategories = db.getMangaCategoriesForCategory(category).await()
        mangaToRemove.forEach { m ->
            val mangaCategory = mangaCategories.find { it.manga_id == m.id }
            if (mangaCategory != null) {
                db.deleteMangaCategory(mangaCategory).await()
            }
        }
        db.insertMangasCategories(mangaToAdd.filter { m ->
            mangaCategories.none { it.manga_id == m.id }
        }.map { MangaCategory.create(it, category) }).await()

        return (mangaCategories.map { it.manga_id } - request.remove + request.add).toSet().toTypedArray()
    }

    private suspend fun validateMutationRequest(categoryList: List<Category>, request: WMutateCategoryRequest) {
        if (categoryList.any { it.name.equals(request.name, true) })
            abort(409, NAME_CONFLICT)
        if (request.order != null && categoryList.any { it.order == request.order })
            abort(409, ORDER_CONFLICT)
    }


    // TODO Maybe change web category model to use 32bit integers instead of longs?
    suspend fun Category.asWeb() = WCategory(
            id!!,
            db.getMangaCategoriesForCategory(this).await().map {
                it.manga_id
            },
            name,
            order
    )

    companion object {
        private const val DEFAULT_CATEGORY_NAME = "Default"
    }
}