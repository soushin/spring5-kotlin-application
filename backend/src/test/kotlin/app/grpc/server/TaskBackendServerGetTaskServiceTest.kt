package app.grpc.server

import app.WebAppException
import app.entity.Task
import app.grpc.handler.context.GRpcLogContextHandler
import app.grpc.handler.log.GRpcLogBuilder
import app.grpc.interceptor.ExceptionFilter
import app.grpc.server.gen.task.TaskInbound
import app.grpc.server.gen.task.TaskServiceGrpc
import app.service.*
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.kotlintest.matchers.shouldBe
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.time.LocalDateTime

@RunWith(PowerMockRunner::class)
@PrepareForTest(GRpcLogContextHandler::class)
class TaskBackendServerGetTaskServiceTest {

    private val UNIQUE_SERVER_NAME = "in-process server for " + TaskBackendServerGetTaskServiceTest::class
    lateinit var inProcessServer: Server
    lateinit var inProcessChannel: ManagedChannel

    lateinit var getTaskService: GetTaskService
    lateinit var getTaskListService: GetTaskListService
    lateinit var createTaskService: CreateTaskService
    lateinit var updateTaskService: UpdateTaskService
    lateinit var deleteTaskService: DeleteTaskService
    lateinit var finishTaskService: FinishTaskService
    lateinit var target: TaskBackendServer

    @Before
    fun setUp() {
        getTaskService = mock(GetTaskServiceImpl::class)
        getTaskListService = mock(GetTaskListServiceImpl::class)
        createTaskService = mock(CreateTaskServiceImpl::class)
        updateTaskService = mock(UpdateTaskServiceImpl::class)
        deleteTaskService = mock(DeleteTaskServiceImpl::class)
        finishTaskService = mock(FinishTaskServiceImpl::class)

        target = TaskBackendServer(getTaskService, getTaskListService, createTaskService, updateTaskService,
                deleteTaskService, finishTaskService)
        inProcessServer = InProcessServerBuilder
                .forName(UNIQUE_SERVER_NAME)
                .addService(target)
                .intercept(ExceptionFilter())
                .directExecutor()
                .build()
        inProcessChannel = InProcessChannelBuilder.forName(UNIQUE_SERVER_NAME).directExecutor().build()

        inProcessServer.start()
    }

    @After
    fun tearDown() {
        inProcessChannel.shutdownNow()
        inProcessServer.shutdownNow()
    }

    @Test
    fun getProducts_onCompleted() {

        val taskId = 1L
        val request = TaskInbound.newBuilder().setTaskId(taskId.toInt()).build()

        val command = GetTaskCommand(taskId)
        val now = LocalDateTime.now()
        val task = Task(taskId.toInt(), "mocked Task", now, now, now)

        // mock
        mockStatic(GRpcLogContextHandler::class)
        Mockito.`when`(GRpcLogContextHandler.getLog()).thenReturn(GRpcLogBuilder())
        Mockito.`when`(getTaskService(command)).thenReturn(task)

        // request server
        val blockingStub = TaskServiceGrpc.newBlockingStub(inProcessChannel)
        val actual = blockingStub.getTaskService(request)

        // assertion
        actual.taskId shouldBe 1
        actual.title shouldBe "mocked Task"
    }

    @Test
    fun getProducts_NOT_FOUND() {

        val taskId = 1L
        val request = TaskInbound.newBuilder().setTaskId(taskId.toInt()).build()

        val command = GetTaskCommand(taskId)

        // mock
        mockStatic(GRpcLogContextHandler::class)
        Mockito.`when`(GRpcLogContextHandler.getLog()).thenReturn(GRpcLogBuilder())
        Mockito.`when`(getTaskService(command)).thenThrow(WebAppException.NotFoundException("not found"))

        try {
            // request server
            val blockingStub = TaskServiceGrpc.newBlockingStub(inProcessChannel)
            blockingStub.getTaskService(request)
        } catch (e: StatusRuntimeException) {
            // assertion
            e.status.code shouldBe Status.NOT_FOUND.code
            e.message shouldBe "NOT_FOUND: not found"
        }
    }

    @Test
    fun getProducts_INVALID_ARGUMENT() {

        val taskId = 0L
        val request = TaskInbound.newBuilder().setTaskId(taskId.toInt()).build()

        try {
            // request server
            val blockingStub = TaskServiceGrpc.newBlockingStub(inProcessChannel)
            blockingStub.getTaskService(request)
        } catch (e: StatusRuntimeException) {
            // assertion
            e.status.code shouldBe Status.INVALID_ARGUMENT.code
            e.message shouldBe "INVALID_ARGUMENT: grpc server error, invalid request"
        }
    }
}
