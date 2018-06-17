package xyz.nulldev.ts.api.v2.http

import com.fasterxml.jackson.annotation.JsonInclude

sealed class Response(val success: Boolean) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Success(val data: Any? = null) : Response(true)
    class Error(val message: String) : Response(false)
}