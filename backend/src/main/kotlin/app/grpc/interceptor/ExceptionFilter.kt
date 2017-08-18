package app.grpc.interceptor

import app.WebAppException
import app.repository.RepositoryException
import io.grpc.*
import mu.KotlinLogging
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.stereotype.Component

/**
 *
 * @author nsoushi
 */
@Component
class ExceptionFilter : ServerInterceptor {

    private val logger = KotlinLogging.logger {}

    override fun <ReqT : Any?, RespT : Any?> interceptCall(call: ServerCall<ReqT, RespT>?, headers: Metadata?,
                                                           next: ServerCallHandler<ReqT, RespT>?): ServerCall.Listener<ReqT> {
        return object : ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(next?.startCall(call, headers)!!) {

            override fun onHalfClose() {
                try {
                    super.onHalfClose()
                } catch (ex: RuntimeException) {
                    handleException(call, headers, ex)
                    throw ex
                }
            }

            override fun onReady() {
                try {
                    super.onReady()
                } catch (ex: RuntimeException) {
                    handleException(call, headers, ex)
                    throw ex
                }

            }
        }
    }

    private fun <ReqT, RespT> handleException(call: ServerCall<ReqT, RespT>?, headers: Metadata?, ex: Exception) {

        logger.error(ex) { ex.message }

        when (ex) {
            is RepositoryException.NotFoundException -> call?.close(
                    Status.fromCode(Status.NOT_FOUND.code).withDescription(ex.message), headers)
            is RepositoryException.ConflictException -> call?.close(
                    Status.fromCode(Status.ALREADY_EXISTS.code).withDescription(ex.message), headers)
            is WebAppException.BadRequestException -> call?.close(
                    Status.fromCode(Status.INVALID_ARGUMENT.code).withDescription(ex.message), headers)
            is WebAppException.NotFoundException -> call?.close(
                    Status.fromCode(Status.NOT_FOUND.code).withDescription(ex.message), headers)
            is EmptyResultDataAccessException -> call?.close(
                    Status.fromCode(Status.NOT_FOUND.code).withDescription("data not found."), headers)
            else -> call?.close(Status.fromCode(Status.INTERNAL.code).withDescription(ex.message), headers)
        }
    }
}
