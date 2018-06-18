package xyz.nulldev.ts.api.v2.java.impl.util

import eu.kanade.tachiyomi.data.database.DatabaseHelper
import xyz.nulldev.ts.ext.kInstanceLazy

class DbMapper<IdType, T : Any>(val ids: List<IdType>,
                                val dbGetter: (IdType) -> T?,
                                val dbSetter: (T) -> Unit) {
    private val db: DatabaseHelper by kInstanceLazy()

    fun <O> mapGet(mapper: (T) -> O): List<O> {
        return ids.map {
            val res = dbGetter(it)
            checkNotNull(res) { "Object with id $it no longer exists!" }
            mapper(res!!)
        }
    }

    fun <O> mapSet(toSet: List<O?>, mapper: (T, O) -> Unit) {
        db.inTransaction {
            ids.mapIndexed { index, idType ->
                val thisSet = toSet[index] ?: return@mapIndexed null

                val res = dbGetter(idType)
                checkNotNull(res) { "Object with id $idType no longer exists!" }
                mapper(res!!, thisSet)
                res
            }.forEach { if(it != null) dbSetter(it) }
        }
    }

    fun mapDelete(mapper: (T) -> Unit) {
        db.inTransaction {
            ids.forEach {
                dbGetter(it)?.let {
                    mapper(it)
                }
            }
        }
    }
}