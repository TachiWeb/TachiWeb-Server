package xyz.nulldev.ts.api.v3.operations.categories

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Category
import eu.kanade.tachiyomi.data.database.models.MangaCategory
import io.vertx.core.Vertx
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import org.eclipse.jetty.http.HttpStatus
import xyz.nulldev.ts.api.v3.OperationGroup
import xyz.nulldev.ts.api.v3.models.categories.*
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
        routerFactory.op(::editCategories.name, ::editCategories)
        routerFactory.opWithParams(::getCategory.name, CATEGORY_ID_PARAM, ::getCategory)
        routerFactory.opWithParams(::editCategory.name, CATEGORY_ID_PARAM, ::editCategory)
        routerFactory.opWithParams(::deleteCategory.name, CATEGORY_ID_PARAM, ::deleteCategory)
        routerFactory.opWithParams(::editCategoryManga.name, CATEGORY_ID_PARAM, ::editCategoryManga)
    }

    suspend fun getCategories(): List<WCategory> {
        return db.getCategories().await().map {
            it.asWeb()
        }
    }

    suspend fun editCategories(requests: List<WBatchMutateCategoryRequest>): List<WCategory> {
        val dbCategories = db.getCategories().await()

        val resultingCategories = requests.map { request ->
            (dbCategories.find { it.id == request.id }
                    ?: abort(HttpStatus.NOT_FOUND_404, request.id.toString())).apply {
                request.name?.let { name = it }
                request.order?.let { order = it }
            }
        }

        validateCategoryList(dbCategories)

        db.insertCategories(resultingCategories).await()

        return resultingCategories.map { it.asWeb() }
    }

    suspend fun createCategory(request: WMutateCategoryRequest): WCategory {
        val existingCategories = db.getCategories().await()
        validateMutationRequest(existingCategories, -1, request)

        val categoryName = request.name ?: run {
            var currentCategoryIndex = 1
            var newCategoryName: String
            do {
                newCategoryName = "Category ${currentCategoryIndex++}"
            } while (existingCategories.any { it.name.equals(newCategoryName, true) })
            newCategoryName
        }

        val newCategory = Category.create(categoryName).apply {
            order = request.order ?: ((existingCategories.maxBy { it.order }?.order ?: 0) + 1)
        }
        newCategory.id = db.insertCategory(newCategory).await().insertedId()!!.toInt()

        return newCategory.asWeb()
    }

    suspend fun getCategory(categoryId: Int): WCategory {
        return db.getCategory(categoryId).await()?.asWeb() ?: notFound()
    }

    suspend fun editCategory(categoryId: Int, request: WMutateCategoryRequest): WCategory {
        val existingCategories = db.getCategories().await()
        validateMutationRequest(existingCategories, categoryId, request)

        val category = db.getCategory(categoryId).await()?.apply {
            request.name?.let { name = it }
            request.order?.let { order = it }
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

    private fun validateMutationRequest(categoryList: List<Category>, curId: Int, request: WMutateCategoryRequestBase) {
        val categoryListExcludingCurrent = categoryList.filter { it.id != curId }
        if (categoryListExcludingCurrent.any { it.name.equals(request.name, true) })
            abort(409, NAME_CONFLICT)
        if (request.order != null && categoryListExcludingCurrent.any { it.order == request.order })
            abort(409, ORDER_CONFLICT)
    }

    private fun validateCategoryList(categoryList: List<Category>) {
        val seenCategories = mutableSetOf<String>()
        val seenOrders = mutableSetOf<Int>()
        categoryList.forEach {
            val lowerName = it.nameLower
            if (lowerName in seenCategories) abort(HttpStatus.CONFLICT_409, NAME_CONFLICT)
            if (it.order in seenOrders) abort(HttpStatus.CONFLICT_409, ORDER_CONFLICT)
            seenCategories += lowerName
            seenOrders += it.order
        }
    }

    suspend fun Category.asWeb() = WCategory(
            id!!,
            db.getMangaCategoriesForCategory(this).await().map {
                it.manga_id
            },
            name,
            order
    )
}