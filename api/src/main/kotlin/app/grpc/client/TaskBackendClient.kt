package app.grpc.client

import app.config.AppProperties
import app.grpc.GrpcException
import app.grpc.server.gen.task.TaskInbound
import app.grpc.server.gen.task.TaskOutbound
import app.grpc.server.gen.task.TaskServiceGrpc
import com.github.kittinunf.result.Result
import io.grpc.netty.NettyChannelBuilder
import org.springframework.stereotype.Component
import valencia.currency.gateway.grpc.client.ShutdownLoan

/**
 *
 * @author nsoushi
 */
@Component
class TaskBackendClient(private val appProperties: AppProperties) {

    fun getTask(taskId: Long): Result<TaskOutbound, GrpcException> =
            ShutdownLoan.using(getChannel(), { channel ->
                val stub = TaskServiceGrpc.newBlockingStub(getChannel())
                val msg = TaskInbound.newBuilder().setTaskId(taskId.toInt()).build()
                Result.Success(stub.getTaskService(msg))
            })

    private fun getChannel() = NettyChannelBuilder.forAddress(appProperties.grpc.backend.host, appProperties.grpc.backend.port)
            // for testing
            .usePlaintext(true)
            .build()
}