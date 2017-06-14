package app

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 *
 * @author nsoushi
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
open class SystemException(message: String, errorItems: List<ErrorItem>?, ex: Exception?) : RuntimeException(message, ex) {

    private val logger = KotlinLogging.logger {}

    constructor(message: String, errorItems: List<ErrorItem>?): this(message, errorItems, null)

    val errorItems: List<ErrorItem> by lazy {
        errorItems ?: listOf(ErrorItem(message, null, null))
    }
}

sealed class WebAppException : SystemException {

    constructor(message: String, errorItems: List<ErrorItem>?) : super(message, errorItems)

    /**
     * NotFoundを表現する汎用的な例外です。
     */
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class NotFoundException : WebAppException {
        constructor(message: String) : super(message, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, errorItems)
    }

    /**
     * Conflictを表現する汎用的な例外です。
     */
    @ResponseStatus(value = HttpStatus.CONFLICT)
    class ConflictException : WebAppException {
        constructor(message: String) : super(message, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, errorItems)
    }

    /**
     * 認証例外です。
     */
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    class UnauthorizedException : WebAppException {
        constructor(message: String) : super(message, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, errorItems)
    }

    /**
     * 入力値例外です。
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    class BadRequestException : WebAppException {
        constructor(message: String) : super(message, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, errorItems)
    }

    /**
     * バッチ処理例外です。
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    class JobException : WebAppException {
        constructor(message: String) : super(message, null)
        constructor(message: String, errorItems: List<ErrorItem>?) : super(message, errorItems)
    }
}


data class ErrorItem(
        val message: String,
        val errorCode: String?,
        val field: String?
)