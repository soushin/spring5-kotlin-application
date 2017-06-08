package app.grpc

import app.ConflictException
import app.NotFoundException
import app.SystemException

/**
 *
 * @author nsoushi
 */
sealed class GrpcException : SystemException {

    constructor(message: String) : super(message)

    class NotFoundException : GrpcException {
        constructor(message: String) : super(message)
    }

    class BadRequestException : GrpcException {
        constructor(message: String) : super(message)
    }

    class ConflictException : GrpcException {
        constructor(message: String) : super(message)
    }

    companion object {
        fun handle(error: GrpcException): SystemException {
            return when (error) {
                is GrpcException.NotFoundException -> NotFoundException(error.message!!)
                is GrpcException.ConflictException -> ConflictException(error.message!!)
                is GrpcException.BadRequestException -> BadRequestException(error.message!!)
            }
        }
    }
}