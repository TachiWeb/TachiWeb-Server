package xyz.nulldev.ts.api.v3.util

import com.fasterxml.jackson.databind.ObjectMapper

fun ObjectMapper.cleanJson(json: String): String {
    return writeValueAsString(readTree(json))
}