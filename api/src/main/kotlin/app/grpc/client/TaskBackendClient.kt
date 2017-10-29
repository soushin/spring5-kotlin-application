package app.grpc.client

import app.config.AppProperties
import app.grpc.GrpcException
import app.grpc.GrpcException.Companion.with
import app.grpc.server.gen.task.*
import com.github.kittinunf.result.Result
import com.google.protobuf.UInt32Value
import io.grpc.Status
import io.grpc.netty.NettyChannelBuilder
import kotlinx.coroutines.experimental.*
import mu.KotlinLogging
import org.springframework.stereotype.Component

/**
 *
 * @author nsoushi
 */
@Component
class TaskBackendClient(private val appProperties: AppProperties) {

    private val logger = KotlinLogging.logger {}

    suspend fun getTask(taskId: Long): TaskOutbound =
            async(CommonPool) {
                try {
                    val outbound = ShutdownLoan.using(getChannel(), { channel ->
                        val msg = GetTaskInbound.newBuilder().setTaskId(taskId.toInt()).build()
                        TaskServiceGrpc.newBlockingStub(channel).getTaskService(msg)
                    })
                    Result.Success<TaskOutbound, GrpcException>(outbound)
                } catch (e: Exception) {
                    val status = Status.fromThrowable(e)
                    logger.error(e) { "gRPC server error, code:{%d}, description:{%s}".format(status.code.value(), status.description) }
                    Result.Failure<TaskOutbound, GrpcException>(status with status.description)
                }
            }.await().fold({ it }, { throw it })

    suspend fun getTaskList(): List<TaskOutbound> =
            async(CommonPool) {
                try {
                    val outbound = ShutdownLoan.using(getChannel(), { channel ->
                        val msg = FindTaskInbound.newBuilder().setPage(UInt32Value.newBuilder().setValue(10).build()).build()
                        TaskServiceGrpc.newBlockingStub(channel).findTaskService(msg).asSequence().map { it }.toList()
                    })
                    Result.Success<List<TaskOutbound>, GrpcException>(outbound)
                } catch (e: Exception) {
                    val status = Status.fromThrowable(e)
                    logger.error(e) { "gRPC server error, code:{%d}, description:{%s}".format(status.code.value(), status.description) }
                    Result.Failure<List<TaskOutbound>, GrpcException>(status with status.description)
                }
            }.await().fold({ it }, { throw it })

    fun createTask(title: String): TaskOutbound =
            try {
                ShutdownLoan.using(getChannel(), { channel ->
                    val msg = CreateTaskInbound.newBuilder().setTitle(title).build()
                    TaskServiceGrpc.newBlockingStub(channel).createTaskService(msg)
                })
            } catch (e: Exception) {
                val status = Status.fromThrowable(e)
                logger.error(e) { "gRPC server error, code:{%d}, description:{%s}".format(status.code.value(), status.description) }
                throw status with status.description
            }

    fun updateTask(id: Long, title: String): TaskOutbound =
            try {
                ShutdownLoan.using(getChannel(), { channel ->
                    val msg = UpdateTaskInbound.newBuilder().setTaskId(id.toInt()).setTitle(title).build()
                    TaskServiceGrpc.newBlockingStub(channel).updateTaskService(msg)
                })
            } catch (e: Exception) {
                val status = Status.fromThrowable(e)
                logger.error(e) { "gRPC server error, code:{%d}, description:{%s}".format(status.code.value(), status.description) }
                throw status with status.description
            }

    fun deleteTask(id: Long): TaskOutbound =
            try {
                ShutdownLoan.using(getChannel(), { channel ->
                    val msg = GetTaskInbound.newBuilder().setTaskId(id.toInt()).build()
                    TaskServiceGrpc.newBlockingStub(channel).deleteTaskService(msg)
                })
            } catch (e: Exception) {
                val status = Status.fromThrowable(e)
                logger.error(e) { "gRPC server error, code:{%d}, description:{%s}".format(status.code.value(), status.description) }
                throw status with status.description
            }

    fun finishTask(id: Long): TaskOutbound =
            try {
                ShutdownLoan.using(getChannel(), { channel ->
                    val msg = GetTaskInbound.newBuilder().setTaskId(id.toInt()).build()
                    TaskServiceGrpc.newBlockingStub(channel).finishTaskService(msg)
                })
            } catch (e: Exception) {
                val status = Status.fromThrowable(e)
                logger.error(e) { "gRPC server error, code:{%d}, description:{%s}".format(status.code.value(), status.description) }
                throw status with status.description
            }

    private fun getChannel() = NettyChannelBuilder.forAddress(appProperties.grpc.backend.host, appProperties.grpc.backend.port)
            // for testing
            .usePlaintext(true)
            .build()
}
