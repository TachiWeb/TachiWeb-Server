package xyz.nulldev.ts.api.v2.http.extensions

import xyz.nulldev.ts.api.v2.http.BaseController
import xyz.nulldev.ts.api.v2.http.Response
import xyz.nulldev.ts.api.v2.http.jvcompat.*
import xyz.nulldev.ts.api.v2.java.model.extensions.ExtensionCollection
import java.io.File

object ExtensionsController : BaseController() {
    private val PARAM_SIGNATURE_HASH = "signature_hash"
    private val FILE_EXTENSION = "extension"

    //TODO Swap to Javalin attribute passing
    fun prepareExtensionsAttributes(ctx: Context) {
        val extensionsParam = ctx.param(EXTENSIONS_PARAM)

        ctx.attribute(EXTENSIONS_ATTR, if(extensionsParam != null)
            api.extensions.get(*extensionsParam.split(",").map {
                it.trim()
            }.toTypedArray())
        else
            api.extensions.getAll()
        )
    }

    private val EXTENSIONS_PARAM = ":extensions"
    private val EXTENSIONS_ATTR = "extensions"

    fun getName(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        getApiField(ctx,
                EXTENSIONS_ATTR,
                ExtensionCollection::pkgName,
                ExtensionCollection::name,
                ExtensionName::class)
    }

    fun getStatus(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        getApiField(ctx,
                EXTENSIONS_ATTR,
                ExtensionCollection::pkgName,
                ExtensionCollection::status,
                ExtensionStatus::class)
    }

    fun getVersionName(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        getApiField(ctx,
                EXTENSIONS_ATTR,
                ExtensionCollection::pkgName,
                ExtensionCollection::versionName,
                ExtensionVersionName::class)
    }

    fun getVersionCode(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        getApiField(ctx,
                EXTENSIONS_ATTR,
                ExtensionCollection::pkgName,
                ExtensionCollection::versionCode,
                ExtensionVersionCode::class)
    }

    fun getSignatureHash(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        getApiField(ctx,
                EXTENSIONS_ATTR,
                ExtensionCollection::pkgName,
                ExtensionCollection::signatureHash,
                ExtensionSignatureHash::class)
    }

    fun getLang(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        getApiField(ctx,
                EXTENSIONS_ATTR,
                ExtensionCollection::pkgName,
                ExtensionCollection::lang,
                ExtensionLang::class)
    }

    fun getSources(ctx: Context) {
        val attr = ctx.attribute<ExtensionCollection>(EXTENSIONS_ATTR)

        ctx.json(Response.Success(attr.sources.mapIndexed { index, data ->
            ExtensionSources(attr.pkgName[index], data?.map { it.id.toString() })
        }))
    }

    fun getHasUpdate(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        getApiField(ctx,
                EXTENSIONS_ATTR,
                ExtensionCollection::pkgName,
                ExtensionCollection::hasUpdate,
                ExtensionHasUpdate::class)
    }

    fun getIcon(ctx: Context) {
        val attr = ctx.attribute<ExtensionCollection>(EXTENSIONS_ATTR)

        if(attr.size > 1) {
            ctx.json(Response.Error("This endpoint cannot be used on multiple extensions simultaneously!"))
        } else if(attr.size == 0) {
            ctx.json(Response.Error("Unable to find extension!"))
        } else {
            val icon = attr.first().icon

            if(icon == null) {
                ctx.json(Response.Error("This extension has no icon!"))
            } else {
                icon.open().use {
                    ctx.result(it)
                }
            }
        }
    }

    fun getExtension(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        ctx.json(Response.Success(ctx.attribute<ExtensionCollection>(EXTENSIONS_ATTR).map {
            SerializableExtensionModel(it)
        }))
    }

    fun delete(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        val attr = ctx.attribute<ExtensionCollection>(EXTENSIONS_ATTR)

        attr.delete()

        ctx.json(Response.Success())
    }

    fun install(ctx: Context) {
        prepareExtensionsAttributes(ctx)

        val attr = ctx.attribute<ExtensionCollection>(EXTENSIONS_ATTR)

        attr.install()

        ctx.json(Response.Success(attr.map { SerializableExtensionModel(it) }))
    }

    fun reloadLocal(ctx: Context) {
        api.extensions.reloadLocal()

        ctx.json(Response.Success())
    }

    fun reloadAvailable(ctx: Context) {
        api.extensions.reloadAvailable()

        ctx.json(Response.Success())
    }

    fun trust(ctx: Context) {
        val signature = ctx.queryParam(PARAM_SIGNATURE_HASH)
                ?: run {
                    ctx.json(Response.Error("No signature hash provided!"))
                    return
                }

        api.extensions.trust(signature)

        ctx.json(Response.Success())
    }

    fun installExternal(ctx: Context) {
        val file = ctx.uploadedFile(FILE_EXTENSION)
                ?: run {
                    ctx.json(Response.Error("No extension file provided!"))
                    return
                }

        val tmp = File.createTempFile("extension-install", ".apk")
        tmp.deleteOnExit()

        try {
            file.content.use { input ->
                tmp.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            api.extensions.installExternal(tmp)
            api.extensions.reloadLocal()
        } finally {
            tmp.delete()
        }
    }
}