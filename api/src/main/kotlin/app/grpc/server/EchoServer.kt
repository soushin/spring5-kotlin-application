package app.grpc.server

import app.grpc.client.EchoBackendClient
import app.grpc.server.gen.echo.EchoMessage
import app.grpc.server.gen.echo.EchoServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.util.*

/**
 *
 * @author nsoushi
 */
@Service
class EchoServer(private val echoBackendClient: EchoBackendClient) : EchoServiceGrpc.EchoServiceImplBase() {

    private val logger = KotlinLogging.logger {}

    override fun echoService(request: EchoMessage?, responseObserver: StreamObserver<EchoMessage>?) {

        val res = echoBackendClient.request(Optional.of("hello"))

        res.fold({
            success ->
            val msg = EchoMessage.newBuilder().setMessage("echo \\$success/").build()
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        }, {
            _ ->
            logger.error { "gRPC backend error, echo error." }
            responseObserver?.onError(
                    Status.INVALID_ARGUMENT.withDescription("echo error.").asRuntimeException())
        })
    }
}