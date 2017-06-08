package app.grpc.client

import app.config.AppProperties
import app.grpc.GrpcException
import app.grpc.server.gen.echo.EchoMessage
import app.grpc.server.gen.echo.EchoServiceGrpc
import com.github.kittinunf.result.Result
import io.grpc.netty.NettyChannelBuilder
import org.springframework.stereotype.Component
import valencia.currency.gateway.grpc.client.ShutdownLoan
import java.util.*

/**
 *
 * @author nsoushi
 */
@Component
class EchoBackendClient(private val appProperties: AppProperties) {

    fun request(req: Optional<String>): Result<String, GrpcException> {

        if (!req.isPresent)
            return Result.Failure(GrpcException.BadRequestException("bad request."))

        return ShutdownLoan.using(getChannel(), { channel ->
            val stub = EchoServiceGrpc.newBlockingStub(getChannel())
            val msg = EchoMessage.newBuilder().setMessage(req.get()).build()
            Result.Success(stub.echoService(msg).message)
        })
    }

    private fun getChannel() = NettyChannelBuilder.forAddress(appProperties.grpc.backend.host, appProperties.grpc.backend.port)
            // for testing
            .usePlaintext(true)
            .build()
}