package xyz.nulldev.ts.api.v3.operations.preferences

import android.preference.PreferenceManager
import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import xyz.nulldev.androidcompat.io.sharedprefs.JsonSharedPreferences
import xyz.nulldev.ts.api.v3.OperationGroup
import xyz.nulldev.ts.api.v3.models.preference.WMutatePreferenceRequest
import xyz.nulldev.ts.api.v3.models.preference.WPreference
import xyz.nulldev.ts.api.v3.op
import xyz.nulldev.ts.api.v3.opWithContext
import xyz.nulldev.ts.api.v3.opWithParams
import xyz.nulldev.ts.api.v3.util.cleanJson
import xyz.nulldev.ts.ext.kInstance

private const val SCHEMA_LOCATION = "/pref-schema.json"
private const val PREFERENCE_KEY_PARAM = "preferenceKey"

class PreferenceOperations(private val vertx: Vertx) : OperationGroup {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(kInstance()) as JsonSharedPreferences

    private val schema by lazy {
        val rawJson = this::class.java.getResourceAsStream(SCHEMA_LOCATION).bufferedReader().use {
            it.readText()
        }
        kInstance<ObjectMapper>().cleanJson(rawJson)
    }

    override fun register(routerFactory: OpenAPI3RouterFactory) {
        routerFactory.op(::getPreferences.name, ::getPreferences)
        routerFactory.opWithParams(::getPreference.name, PREFERENCE_KEY_PARAM, ::getPreference)
        routerFactory.opWithParams(::setPreference.name, PREFERENCE_KEY_PARAM, ::setPreference)
        routerFactory.opWithContext(::getPreferencesSchema.name, ::getPreferencesSchema)
    }

    suspend fun getPreferences() = preferences.all.map {
        WPreference(it.key, it.value)
    }

    suspend fun getPreference(preferenceKey: String) = WPreference(preferenceKey, preferences.get(preferenceKey, null)
            ?: notFound())

    suspend fun setPreference(preferenceKey: String, request: WMutatePreferenceRequest) = preferences.edit()
            .put(preferenceKey, when {
                request.value is List<*> -> request.value.toSet()
                else -> request.value
            })
            .commit()

    suspend fun getPreferencesSchema(context: RoutingContext) {
        context.response().end(schema)
    }
}