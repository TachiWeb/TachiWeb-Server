package xyz.nulldev.ts.api.v3.models.exceptions

class WException : Exception {
    var responseCode: Int? = null
    var content: String? = null

    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
    constructor(message: String, cause: Throwable, enableSuppression: Boolean, writableStackTrace: Boolean) : super(message, cause, enableSuppression, writableStackTrace)

    constructor(responseCode: Int) : super() {
        this.responseCode = responseCode
    }

    constructor(responseCode: Int, enumError: WErrorTypes) : this(responseCode) {
        this.content = "\"${enumError.name}\""
    }
}
