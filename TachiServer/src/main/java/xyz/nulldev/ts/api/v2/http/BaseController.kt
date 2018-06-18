package xyz.nulldev.ts.api.v2.http

import xyz.nulldev.ts.api.v2.http.jvcompat.Context
import xyz.nulldev.ts.api.v2.http.jvcompat.attribute
import xyz.nulldev.ts.api.v2.http.jvcompat.bodyAsClass
import xyz.nulldev.ts.api.v2.http.jvcompat.json
import xyz.nulldev.ts.api.v2.java.Tachiyomi
import xyz.nulldev.ts.api.v2.java.model.ServerAPI
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1

abstract class BaseController {
    protected val api: ServerAPI = Tachiyomi

    fun <C, T, I : Number, H : Any> getApiField(ctx: Context,
                                                attribute: String,
                                                collectionIdField: KProperty1<C, List<I>>,
                                                collectionField: KProperty1<C, List<T>>,
                                                holder: KClass<H>) {
        val attr = ctx.attribute<C>(attribute)

        ctx.json(Response.Success(collectionField.get(attr).mapIndexed { index, data ->
            holder.constructors.first().call(collectionIdField.get(attr)[index], data)
        }))
    }

    inline fun <C, T, I : Number, reified H : Any> setApiField(ctx: Context,
                                                attribute: String,
                                                collectionIdField: KProperty1<C, List<I>>,
                                                collectionField: KMutableProperty1<C, List<T?>>,
                                                holderIdField: KProperty1<H, I>,
                                                holderContentField: KProperty1<H, T>) {
        val attr = ctx.attribute<C>(attribute)

        val new = ctx.bodyAsClass<Array<H>>().toList()

        collectionField.set(attr, collectionIdField.get(attr).map { item ->
            new.find { holderIdField.get(it) == item }?.let {
                holderContentField.get(it)
            }
        })

        ctx.json(Response.Success())
    }
}