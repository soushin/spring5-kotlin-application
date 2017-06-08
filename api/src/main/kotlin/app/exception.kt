package app

/**
 *
 * @author nsoushi
 */
open class SystemException(message: String, ex: Exception?) : RuntimeException(message, ex) {
    constructor(message: String): this(message,null)
}

open class NotFoundException : SystemException {
    constructor(message: String) : super(message)
}

open class BadRequestException : SystemException {
    constructor(message: String) : super(message)
}

open class UnAuthorizedException : SystemException {
    constructor(message: String) : super(message)
}

open class ConflictException : SystemException {
    constructor(message: String) : super(message)
}
