package app.repository

import app.ConflictException
import app.NotFoundException
import app.SystemException

/**
 *
 * @author nsoushi
 */
sealed class RepositoryException : SystemException {

    constructor(message: String) : super(message)

    class NotFoundException : RepositoryException {
        constructor(message: String) : super(message)
    }

    class ConflictException : RepositoryException {
        constructor(message: String) : super(message)
    }

    companion object {
        fun handle(error: RepositoryException): SystemException {
            return when (error) {
                is RepositoryException.NotFoundException -> NotFoundException(error.message!!)
                is RepositoryException.ConflictException -> ConflictException(error.message!!)
            }
        }
    }
}