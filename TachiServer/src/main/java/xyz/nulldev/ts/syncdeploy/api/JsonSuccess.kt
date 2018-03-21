package xyz.nulldev.ts.syncdeploy.api

import com.github.salomonbrys.kotson.jsonObject

class JsonSuccess(vararg val data: Pair<String, *>) {
    override fun toString(): String {
        return jsonObject("success" to true,
                "data" to jsonObject(*data)).toString()
    }
}
