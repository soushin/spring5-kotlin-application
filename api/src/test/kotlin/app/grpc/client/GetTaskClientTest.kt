package app.grpc.client

import app.config.AppProperties
import app.grpc.GrpcException
import app.grpc.server.gen.task.TaskInbound
import app.grpc.server.gen.task.TaskOutbound
import app.grpc.server.gen.task.TaskServiceGrpc
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

        override fun getTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
            responseObserver?.onNext(TaskOutbound.newBuilder()
                    .setTaskId(1)
                    .setTitle("mocked Task")
                    .setFinishedAt("2017-01-01T23:59:59Z")
                    .setCreatedAt("2017-01-02T23:59:59Z")
                    .setUpdatedAt("2017-01-02T23:59:59Z")
                    .build()
            )
            responseObserver?.onCompleted()
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
            actual.finishedAt shouldBe "2017-01-01T23:59:59Z"
            actual.createdAt shouldBe "2017-01-02T23:59:59Z"
            actual.updatedAt shouldBe "2017-01-02T23:59:59Z"
        }
    }

    private class GetTaskServerNotFound : TaskServiceGrpc.TaskServiceImplBase() {

        override fun getTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
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
