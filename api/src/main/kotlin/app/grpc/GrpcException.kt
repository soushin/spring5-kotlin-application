package app.grpc

import app.SystemException
import io.grpc.Status
import io.grpc.Status.*
import org.springframework.http.HttpStatus

/**
 *
 * @author nsoushi
 */
sealed class GrpcException : SystemException {

    constructor(message: String, status: HttpStatus) : super(message, status, null)

    class UnknownException : GrpcException {
        constructor(message: String) : super(message, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    class NotFoundException : GrpcException {
        constructor(message: String) : super(message, HttpStatus.NOT_FOUND)
    }

    class BadRequestException : GrpcException {
        constructor(message: String) : super(message, HttpStatus.BAD_REQUEST)
    }

    class ConflictException : GrpcException {
        constructor(message: String) : super(message, HttpStatus.CONFLICT)
    }

    companion object {
        infix fun Status.with(description: String?): GrpcException =
                if (this.code == NOT_FOUND.code)
                    GrpcException.NotFoundException(description ?: "not found")
                else if (this.code == INVALID_ARGUMENT.code)
                    GrpcException.BadRequestException(description ?: "invalid argument")
                else if (this.code == UNAVAILABLE.code)
                    GrpcException.ConflictException(description ?: "unavailable")
                else
                    GrpcException.UnknownException("unknown error")
    }
}