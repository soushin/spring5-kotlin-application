package app.repository

import app.SystemException
import app.WebAppException

/**
 *
 * @author nsoushi
 */
sealed class RepositoryException : SystemException {

    constructor(message: String) : super(message, null)

    class NotFoundException : RepositoryException {
        constructor(message: String) : super(message)
    }

    class ConflictException : RepositoryException {
        constructor(message: String) : super(message)
    }

    companion object {
        fun handle(error: RepositoryException): SystemException {
            return when (error) {
                is RepositoryException.NotFoundException -> WebAppException.NotFoundException(error.message!!)
                is RepositoryException.ConflictException -> WebAppException.ConflictException(error.message!!)
            }
        }
    }
}