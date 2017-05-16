package xyz.nulldev.ts.ext

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject

/**
 * Created by nulldev on 5/11/17.
 */

private val jsonParser: JsonParser by kInstanceLazy()

fun JSONObject.gson() = jsonParser.parse(this.toString())!!
fun JsonObject.json() = JSONObject(this.toString())
