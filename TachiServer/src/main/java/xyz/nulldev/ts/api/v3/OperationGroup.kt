package xyz.nulldev.ts.api.v3

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import xyz.nulldev.ts.api.v3.models.exceptions.WErrorTypes
import xyz.nulldev.ts.api.v3.models.exceptions.WException

interface OperationGroup {
    fun register(routerFactory: OpenAPI3RouterFactory)

    fun abort(responseCode: Int, enumError: WErrorTypes): Nothing = throw WException(responseCode, enumError)

    fun abort(responseCode: Int): Nothing = throw WException(responseCode)

    fun notFound(): Nothing = abort(404)
    fun notFound(enumError: WErrorTypes): Nothing = abort(404, enumError)

    fun internalError(): Nothing = abort(500)
    fun internalError(enumError: WErrorTypes): Nothing = abort(500, enumError)
}

internal val apiSerializer = ObjectMapper().registerKotlinModule().setSerializationInclusion(JsonInclude.Include.NON_NULL)

internal inline fun <reified Input, reified Output> OpenAPI3RouterFactory.op(
        operationId: String,
        noinline function: suspend (Input) -> Output
) = opWithContext(operationId) { input: Input, _ ->
    function(input)
}

internal inline fun <reified Output> OpenAPI3RouterFactory.op(
        operationId: String,
        noinline function: suspend () -> Output
) = opWithContext(operationId) { _: Unit, _ ->
    function()
}

internal inline fun <reified Output> OpenAPI3RouterFactory.opWithContext(
        operationId: String,
        noinline function: suspend (RoutingContext) -> Output
) = opWithContext(operationId) { _: Unit, rc ->
    function(rc)
}

internal inline fun <reified Input, reified Output> OpenAPI3RouterFactory.opWithContext(
        operationId: String,
        noinline function: suspend (Input, RoutingContext) -> Output
) {
    addHandlerByOperationId(operationId) {
        // TODO Fix this scoping
        GlobalScope.launch {
            val input: Any? = if (Input::class != Unit::class) {
                apiSerializer.readValue<Input>(it.bodyAsString)
            } else Unit

            val response = it.response()

            val result: Any? = try {
                function(input as Input, it)
            } catch (error: WException) {
                response.statusCode = error.responseCode ?: 500

                if (error.content != null)
                    error.content
                else Unit
            }

            if (!response.ended()) {
                if (result !is Unit) {
                    response.end(apiSerializer.writeValueAsString(result))
                } else {
                    response.end()
                }
            }
        }
    }
}

internal inline fun <reified PathParam1, reified Input, reified Output> OpenAPI3RouterFactory.opWithParams(
        operationId: String,
        pathParam1Name: String,
        noinline function: suspend (PathParam1, Input) -> Output
) = opWithContext(operationId) { input: Input, rc ->
    function(rc.pathParamObj(pathParam1Name), input)
}

internal inline fun <reified PathParam1, reified PathParam2, reified Input, reified Output> OpenAPI3RouterFactory.opWithParams(
        operationId: String,
        pathParam1Name: String,
        pathParam2Name: String,
        noinline function: suspend (PathParam1, PathParam2, Input) -> Output
) = opWithContext(operationId) { input: Input, rc ->
    function(rc.pathParamObj(pathParam1Name), rc.pathParamObj(pathParam2Name), input)
}

internal inline fun <reified PathParam1, reified Output> OpenAPI3RouterFactory.opWithParams(
        operationId: String,
        pathParam1Name: String,
        noinline function: suspend (PathParam1) -> Output
) = opWithContext(operationId) { _: Unit, rc ->
    function(rc.pathParamObj(pathParam1Name))
}

internal inline fun <reified PathParam1, reified PathParam2, reified Output> OpenAPI3RouterFactory.opWithParams(
        operationId: String,
        pathParam1Name: String,
        pathParam2Name: String,
        noinline function: suspend (PathParam1, PathParam2) -> Output
) = opWithContext(operationId) { _: Unit, rc ->
    function(rc.pathParamObj(pathParam1Name), rc.pathParamObj(pathParam2Name))
}

internal inline fun <reified PathParam1, reified Input, reified Output> OpenAPI3RouterFactory.opWithParamsAndContext(
        operationId: String,
        pathParam1Name: String,
        noinline function: suspend (PathParam1, Input, RoutingContext) -> Output
) = opWithContext(operationId) { input: Input, rc ->
    function(rc.pathParamObj(pathParam1Name), input, rc)
}

internal inline fun <reified PathParam1, reified PathParam2, reified Input, reified Output> OpenAPI3RouterFactory.opWithParamsAndContext(
        operationId: String,
        pathParam1Name: String,
        pathParam2Name: String,
        noinline function: suspend (PathParam1, PathParam2, Input, RoutingContext) -> Output
) = opWithContext(operationId) { input: Input, rc ->
    function(rc.pathParamObj(pathParam1Name), rc.pathParamObj(pathParam2Name), input, rc)
}

internal inline fun <reified PathParam1, reified Output> OpenAPI3RouterFactory.opWithParamsAndContext(
        operationId: String,
        pathParam1Name: String,
        noinline function: suspend (PathParam1, RoutingContext) -> Output
) = opWithContext(operationId) { _: Unit, rc ->
    function(rc.pathParamObj(pathParam1Name), rc)
}

internal inline fun <reified PathParam1, reified PathParam2, reified Output> OpenAPI3RouterFactory.opWithParamsAndContext(
        operationId: String,
        pathParam1Name: String,
        pathParam2Name: String,
        noinline function: suspend (PathParam1, PathParam2, RoutingContext) -> Output
) = opWithContext(operationId) { _: Unit, rc ->
    function(rc.pathParamObj(pathParam1Name), rc.pathParamObj(pathParam2Name), rc)
}

internal inline fun <reified T> RoutingContext.pathParamObj(name: String): T {
    val param: String? = pathParam(name)

    val result: Any? = when (T::class) {
        String::class -> param
        Int::class -> param?.toInt()
        Long::class -> param?.toLong()
        Boolean::class -> param?.toBoolean()
        Float::class -> param?.toFloat()
        Double::class -> param?.toDouble()
        else -> error("Unknown object type: ${T::class.qualifiedName}")
    }

    return result as? T ?: error("Path param '$param' could not be casted!")
}
