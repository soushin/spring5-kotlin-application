package app.grpc.interceptor

import com.codahale.metrics.MetricRegistry
import io.grpc.*
import mu.KotlinLogging
import org.springframework.stereotype.Component

/**
 *
 * @author nsoushi
 */
@Component
class MetricsInterceptor(private val metricRegistry: MetricRegistry) : ServerInterceptor {

    private val ERROR_METRIC = "server.error"
    private val REQUEST_TIME = "server.request.time"

    private val logger = KotlinLogging.logger {}

    override fun <ReqT : Any?, RespT : Any?> interceptCall(call: ServerCall<ReqT, RespT>?, headers: Metadata?, next: ServerCallHandler<ReqT, RespT>?): ServerCall.Listener<ReqT> {

        val timer = metricRegistry.timer(metricName(REQUEST_TIME, call?.methodDescriptor?.fullMethodName!!.replace("/", "."))).time()

        val serverCall = object : ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {

            override fun close(status: Status?, trailers: Metadata?) {
                val errorMeter = metricRegistry.meter(metricName(ERROR_METRIC, methodDescriptor.fullMethodName.replace("/", ".")))
                if (!status!!.isOk()) {
                    errorMeter.mark()
                    logger.error { "An error occured with %s".format(call.methodDescriptor) }
                }
                timer.stop()
                super.close(status, trailers)
            }
        }

        return next?.startCall(serverCall, headers)!!
    }

    private fun metricName(metric: String, method: String): String {
        return String.format("%s.%s", metric, method)
    }
}