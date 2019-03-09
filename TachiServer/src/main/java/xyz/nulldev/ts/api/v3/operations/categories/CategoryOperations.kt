package xyz.nulldev.ts.api.v3.operations.categories

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import eu.kanade.tachiyomi.data.database.models.Category
import io.vertx.core.Vertx
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import xyz.nulldev.ts.api.v3.OperationGroup
import xyz.nulldev.ts.api.v3.models.categories.WCategory
import xyz.nulldev.ts.api.v3.models.categories.WMutateCategoryRequest
import xyz.nulldev.ts.api.v3.models.exceptions.WErrorTypes.NAME_CONFLICT
import xyz.nulldev.ts.api.v3.models.exceptions.WErrorTypes.ORDER_CONFLICT
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
    }

    suspend fun getCategories(): List<WCategory> {
        return db.getCategories().await().map {
            it.asWeb()
        }
    }

    suspend fun createCategory(request: WMutateCategoryRequest): WCategory {
        val existingCategories = db.getCategories().await()
        validateMutationRequest(existingCategories, request)

        val newCategory = Category.create(request.name).apply {
            order = request.order?.toInt() ?: existingCategories.maxBy { it.order }?.order ?: 1
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
            order = request.order?.toInt() ?: existingCategoriesWithoutSelf.maxBy { it.order }?.order ?: 1
        } ?: notFound()
        db.insertCategory(category).await()
        return category.asWeb()
    }

    suspend fun deleteCategory(categoryId: Int) {
        db.deleteCategory(db.getCategory(categoryId).await() ?: notFound()).await()
    }

    private suspend fun validateMutationRequest(categoryList: List<Category>, request: WMutateCategoryRequest) {
        if (categoryList.any { it.name.equals(request.name, true) })
            abort(409, NAME_CONFLICT)
        if (request.order != null && categoryList.any { it.order.toLong() == request.order })
            abort(409, ORDER_CONFLICT)
    }


    // TODO Maybe change web category model to use 32bit integers instead of longs?
    suspend fun Category.asWeb() = WCategory(
            id!!.toLong(),
            db.getMangaCategoriesForCategory(this).await().map {
                it.manga_id
            },
            name,
            order.toLong()
    )
}