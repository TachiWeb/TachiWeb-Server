package xyz.nulldev.ts.api.http

import com.fasterxml.jackson.annotation.JsonInclude

sealed class Response(val success: Boolean) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class Success(val content: Any? = null) : Response(true)
    class Error(val message: String) : Response(false)
}