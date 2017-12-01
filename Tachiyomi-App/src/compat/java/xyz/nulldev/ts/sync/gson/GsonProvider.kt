package xyz.nulldev.ts.sync.gson

import com.google.gson.GsonBuilder
import xyz.nulldev.ts.sync.protocol.models.common.SyncEntity

object GsonProvider {
    val gson = GsonBuilder()
            .registerTypeAdapter(SyncEntity::class.java, SyncEntityAdapter())
            .setPrettyPrinting() //TODO Remove
            .create() //TODO Add field exclusions
}