package xyz.nulldev.ts.syncdeploy.api

import com.github.salomonbrys.kotson.jsonObject

data class JsonError(val error: String) {
    override fun toString(): String {
        return jsonObject("success" to false,
                "error" to error).toString()
    }
}