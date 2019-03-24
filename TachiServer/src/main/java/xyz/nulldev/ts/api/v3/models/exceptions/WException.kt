package xyz.nulldev.ts.api.v3.models.exceptions

import com.google.common.base.Throwables
import xyz.nulldev.ts.api.v3.models.WError

class WException(val data: DataType) : Exception() {
    constructor(responseCode: Int) : this(responseCode, null)

    constructor(responseCode: Int, enumError: WErrorTypes?) : this(
            DataType.GeneralError(
                    responseCode,
                    enumError?.name
            )
    )

    constructor(responseCode: Int, wError: WError) : this(DataType.ExpectedError(responseCode, wError))

    sealed class DataType(val responseCode: Int) {
        class GeneralError(responseCode: Int, val content: String?) : DataType(responseCode)
        class ExpectedError(responseCode: Int, val wError: WError) : DataType(responseCode)
    }

    companion object {
        fun expected(responseCode: Int,
                     message: String,
                     type: WErrorTypes,
                     throwable: Throwable = Exception(message)) = WException(
                responseCode,
                WError(
                        message,
                        Throwables.getStackTraceAsString(throwable),
                        type.toString()
                )
        )
    }
}
