package app.grpc.client

import app.config.AppProperties
import app.grpc.GrpcException
import app.grpc.server.gen.task.GetTaskInbound
import app.grpc.server.gen.task.TaskOutbound
import app.grpc.server.gen.task.TaskServiceGrpc
import app.util.DateUtil
import com.google.protobuf.Timestamp
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.Status
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.stub.StreamObserver
import io.grpc.util.MutableHandlerRegistry
import io.kotlintest.matchers.shouldBe
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.time.ZoneId

@RunWith(PowerMockRunner::class)
@PrepareForTest(TaskBackendClient::class)
class GetTaskClientTest {

    private val UNIQUE_SERVER_NAME = "in-process server for " + GetTaskClientTest::class
    lateinit var serviceRegistry: MutableHandlerRegistry
    lateinit var inProcessServer: Server
    lateinit var inProcessChannel: ManagedChannel

    lateinit var target: TaskBackendClient

    @Before
    fun setup() {

        serviceRegistry = MutableHandlerRegistry()
        inProcessServer = InProcessServerBuilder
                .forName(UNIQUE_SERVER_NAME).fallbackHandlerRegistry(serviceRegistry).directExecutor().build()
        inProcessChannel = InProcessChannelBuilder.forName(UNIQUE_SERVER_NAME).directExecutor().build()

        val appProperties = AppProperties()
        target = TaskBackendClient(appProperties)

        inProcessServer.start()
    }

    @After
    fun shutdown() {
        inProcessChannel.shutdownNow()
        inProcessServer.shutdownNow()
    }

    private class GetTaskServerOk : TaskServiceGrpc.TaskServiceImplBase() {

        override fun getTaskService(request: GetTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
            responseObserver?.onNext(TaskOutbound.newBuilder()
                    .setTaskId(1)
                    .setTitle("mocked Task")
                    .setFinishedAt(getTimestamp(DateUtil.parse(DateUtil.Format.FULL_UTC)("2017-01-01T23:59:59Z")))
                    .setCreatedAt(getTimestamp(DateUtil.parse(DateUtil.Format.FULL_UTC)("2017-01-02T23:59:59Z")))
                    .setUpdatedAt(getTimestamp(DateUtil.parse(DateUtil.Format.FULL_UTC)("2017-01-02T23:59:59Z")))
                    .build()
            )
            responseObserver?.onCompleted()
        }

        private fun getTimestamp(date: LocalDateTime): Timestamp.Builder {
            return Timestamp.newBuilder().setSeconds(java.sql.Timestamp.valueOf(date).toLocalDateTime()
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        }
    }



    @Test
    fun getTask() {
        serviceRegistry.addService(GetTaskServerOk())

        // mock
        val instance = PowerMockito.spy(target)
        PowerMockito.doReturn(inProcessChannel).`when`(instance, "getChannel")

        runBlocking {

            // assertion
            val actual = instance.getTask(1L)

            actual.taskId shouldBe 1
            actual.title shouldBe "mocked Task"

            val finishedAt = DateUtil.parse(DateUtil.Format.FULL_UTC)("2017-01-01T23:59:59Z")
            val finishedAtTimestamp = java.sql.Timestamp.valueOf(finishedAt).toLocalDateTime()
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            actual.finishedAt.seconds shouldBe finishedAtTimestamp

            val createdAt = DateUtil.parse(DateUtil.Format.FULL_UTC)("2017-01-02T23:59:59Z")
            val createdAtTimestamp = java.sql.Timestamp.valueOf(createdAt).toLocalDateTime()
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            actual.createdAt.seconds shouldBe createdAtTimestamp
            actual.updatedAt.seconds shouldBe createdAtTimestamp
        }
    }

    private class GetTaskServerNotFound : TaskServiceGrpc.TaskServiceImplBase() {

        override fun getTaskService(request: GetTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
            responseObserver?.onError(Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
            responseObserver?.onCompleted()
        }
    }

    @Test(expected = GrpcException::class)
    fun getTask_then_NotFound() {
        serviceRegistry.addService(GetTaskServerNotFound())

        // mock
        val instance = PowerMockito.spy(target)
        PowerMockito.doReturn(inProcessChannel).`when`(instance, "getChannel")

        try {
            runBlocking {
                instance.getTask(1L)
            }
        } catch (e: GrpcException) {
            e.message shouldBe "task not found."
            e.status shouldBe HttpStatus.NOT_FOUND
            throw e
        }
    }
}
