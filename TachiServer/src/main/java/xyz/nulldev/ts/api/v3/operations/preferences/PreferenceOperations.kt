package xyz.nulldev.ts.api.v3.operations.preferences

import android.preference.PreferenceManager
import io.vertx.core.Vertx
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import xyz.nulldev.androidcompat.io.sharedprefs.JsonSharedPreferences
import xyz.nulldev.ts.api.v3.OperationGroup
import xyz.nulldev.ts.api.v3.models.exceptions.WErrorTypes.INVALID_TYPE
import xyz.nulldev.ts.api.v3.models.preference.WPreference
import xyz.nulldev.ts.api.v3.op
import xyz.nulldev.ts.api.v3.opWithContext
import xyz.nulldev.ts.api.v3.opWithParams
import xyz.nulldev.ts.ext.kInstance

private const val SCHEMA_LOCATION = "/pref-schema.json"
private const val PREFERENCE_KEY_PARAM = "preferenceKey"

class PreferenceOperations(private val vertx: Vertx) : OperationGroup {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(kInstance()) as JsonSharedPreferences

    private val schema by lazy {
        this::class.java.getResourceAsStream(SCHEMA_LOCATION).bufferedReader().use {
            it.readText()
        }
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

    suspend fun setPreference(preferenceKey: String, obj: Any) = preferences.edit()
            .put(preferenceKey, if (obj is List<*>) obj.toSet() else if (obj is Set<*>) obj else internalError(INVALID_TYPE))
            .commit()

    suspend fun getPreferencesSchema(context: RoutingContext) {
        context.response().end(schema)
    }
}