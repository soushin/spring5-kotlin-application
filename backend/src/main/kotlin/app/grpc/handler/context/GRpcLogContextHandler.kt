package app.grpc.handler.context

import app.grpc.handler.log.GRpcLogBuilder
import io.grpc.Context

/**
 *
 * @author nsoushi
 */
/**
 *
 * @author nsoushi
 */
object GRpcLogContextHandler {

    private val GRPC_LOG: Context.Key<GRpcLogBuilder> = Context.key("GRPC_LOG")

    @JvmStatic
    fun setLog(ctx: Context, log: GRpcLogBuilder) = ctx.withValue(GRPC_LOG, log)

    @JvmStatic
    fun getLog() =
            try {
                GRPC_LOG.get()
            } catch (e: Exception) {
                GRpcLogBuilder({
                    name { "UnknownName" }
                })
            }
}