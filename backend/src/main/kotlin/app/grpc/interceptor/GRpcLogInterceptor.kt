package app.grpc.interceptor

import app.grpc.handler.context.GRpcLogContextHandler
import app.grpc.handler.log.GRpcLogBuilder
import io.grpc.*
import mu.KotlinLogging
import org.springframework.stereotype.Component

/**
 *
 * @author nsoushi
 */
@Component
class GRpcLogInterceptor : ServerInterceptor {

    private val logger = KotlinLogging.logger {}

    override fun <ReqT : Any?, RespT : Any?> interceptCall(call: ServerCall<ReqT, RespT>?, headers: Metadata?, next: ServerCallHandler<ReqT, RespT>?): ServerCall.Listener<ReqT> {

        val serverName = call?.methodDescriptor?.fullMethodName!!.replace("/", ".")

        val serverCall = object : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

            override fun close(status: Status?, trailers: Metadata?) {

                val log = GRpcLogContextHandler.getLog()

                if (status!!.isOk())
                    log.success { true }

                try {
                    logger.info { log.build().toString() }
                } catch (e: Exception) {
                    logger.warn { "GRpcLogger is not set with %s".format(serverName) }
                }

                super.close(status, trailers)
            }
        }

        val log = GRpcLogBuilder({
            name { serverName }
            remoteAddr { call.attributes.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).toString() }
        })
        val ctx = GRpcLogContextHandler.setLog(Context.current(), log)

        return Contexts.interceptCall(ctx, serverCall, headers, next)
    }
}