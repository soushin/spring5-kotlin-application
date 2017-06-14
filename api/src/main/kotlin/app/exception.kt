package app

import org.springframework.http.HttpStatus

/**
 *
 * @author nsoushi
 */
data class ErrorItem(
        val message: String,
        val errorCode: String?,
        val field: String?
)

open class SystemException(message: String, status: HttpStatus, errorItems: List<ErrorItem>?, ex: Exception?) : RuntimeException(message, ex) {

    constructor(message: String, status: HttpStatus, errorItems: List<ErrorItem>?): this(message, status, errorItems, null)

    val errorItems: List<ErrorItem> by lazy {
        errorItems ?: listOf(ErrorItem(message, null, null))
    }

    val status: HttpStatus by lazy {
        status
    }
}

sealed class WebAppException : SystemException {

    constructor(message: String, status: HttpStatus, errorItems: List<ErrorItem>?) : super(message, status, errorItems)

    open class InternalServerException : WebAppException {
        constructor(message: String) : super(message, HttpStatus.INTERNAL_SERVER_ERROR, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, HttpStatus.INTERNAL_SERVER_ERROR, errorItems)
    }

    open class NotFoundException : WebAppException {
        constructor(message: String) : super(message, HttpStatus.NOT_FOUND, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, HttpStatus.NOT_FOUND, errorItems)
    }

    open class ConflictException : WebAppException {
        constructor(message: String) : super(message, HttpStatus.CONFLICT, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, HttpStatus.CONFLICT, errorItems)
    }

    open class UnauthorizedException : WebAppException {
        constructor(message: String) : super(message, HttpStatus.UNAUTHORIZED, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, HttpStatus.UNAUTHORIZED, errorItems)
    }

    open class BadRequestException : WebAppException {
        constructor(message: String) : super(message, HttpStatus.BAD_REQUEST, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, HttpStatus.BAD_REQUEST, errorItems)
    }
}
