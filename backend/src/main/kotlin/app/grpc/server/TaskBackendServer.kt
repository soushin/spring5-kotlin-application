package app.grpc.server

import app.WebAppException
import app.entity.Task
import app.grpc.handler.context.GRpcLogContextHandler
import app.grpc.server.gen.task.*
import app.grpc.validator.GRpcInboundValidator
import app.service.*
import app.util.DateConverter.Format.FULL_UTC
import app.util.DateConverter.to
import io.grpc.Status
import io.grpc.stub.StreamObserver
import mu.KotlinLogging

/**
 *
 * @author nsoushi
 */
@GRpcService
class TaskBackendServer(private val getTaskService: GetTaskService,
                        private val getTaskListService: GetTaskListService,
                        private val createTaskService: CreateTaskService,
                        private val updateTaskService: UpdateTaskService,
                        private val deleteTaskService: DeleteTaskService,
                        private val finishTaskService: FinishTaskService) : TaskServiceGrpc.TaskServiceImplBase() {

    private val logger = KotlinLogging.logger {}

    private fun getOutbound(entity: Task): TaskOutbound {
        val builder = TaskOutbound.newBuilder()
                .setTaskId(entity.id!!)
                .setTitle(entity.title)
                .setCreatedAt(entity.createdAt to FULL_UTC)
                .setUpdatedAt(entity.updatedAt to FULL_UTC)

        if (entity.finishedAt != null)
            builder.setFinishedAt(entity.finishedAt to FULL_UTC)

        return builder.build()
    }

    override fun getTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val taskId = GRpcInboundValidator.validTaskInbound(request)

            val log = GRpcLogContextHandler.getLog()
            log.elem { "taskId" to taskId }

            val task = getTaskService(GetTaskCommand(taskId.toLong()))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        } catch (e: WebAppException.BadRequestException) {
            logger.error { "gRPC server error, invalid request." }
            responseObserver?.onError(
                    Status.INVALID_ARGUMENT.withDescription("invalid request.").asRuntimeException())
        }
    }

    override fun getTaskListService(request: TaskListInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val (page) = GRpcInboundValidator.validTaskListInbound(request)

            val log = GRpcLogContextHandler.getLog()
            log.elem { "page" to page }

            getTaskListService(GetTaskListCommand(page.toInt()))().forEach {
                val msg = getOutbound(it)
                responseObserver?.onNext(msg)
            }
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        } catch (e: WebAppException.BadRequestException) {
            logger.error { "gRPC server error, invalid request." }
            responseObserver?.onError(
                    Status.INVALID_ARGUMENT.withDescription("invalid request.").asRuntimeException())
        }
    }

    override fun createTaskService(request: CreateTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val (title) = GRpcInboundValidator.validCreateTaskInbound(request)

            val log = GRpcLogContextHandler.getLog()
            log.elem { "title" to title }

            val task = createTaskService(CreateTaskCommand(title))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: Exception) {
            logger.error { "gRPC server error." }
            responseObserver?.onError(
                    Status.INTERNAL.withDescription("task error.").asRuntimeException())
        } catch (e: WebAppException.BadRequestException) {
            logger.error { "gRPC server error, invalid request." }
            responseObserver?.onError(
                    Status.INVALID_ARGUMENT.withDescription("invalid request.").asRuntimeException())
        }
    }

    override fun updateTaskService(request: UpdateTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val (taskId, title) = GRpcInboundValidator.validUpdateTaskInbound(request)

            val log = GRpcLogContextHandler.getLog()
            log.elem { "taskId" to taskId }
            log.elem { "title" to title }

            val task = updateTaskService(UpdateTaskCommand(taskId.toLong(), title))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        } catch (e: WebAppException.BadRequestException) {
            logger.error { "gRPC server error, invalid request." }
            responseObserver?.onError(
                    Status.INVALID_ARGUMENT.withDescription("invalid request.").asRuntimeException())
        }
    }

    override fun deleteTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val taskId = GRpcInboundValidator.validTaskInbound(request)

            val log = GRpcLogContextHandler.getLog()
            log.elem { "taskId" to taskId }

            val task = deleteTaskService(DeleteTaskCommand(taskId.toLong()))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        } catch (e: WebAppException.BadRequestException) {
            logger.error { "gRPC server error, invalid request." }
            responseObserver?.onError(
                    Status.INVALID_ARGUMENT.withDescription("invalid request.").asRuntimeException())
        }
    }

    override fun finishTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val taskId = GRpcInboundValidator.validTaskInbound(request)

            val log = GRpcLogContextHandler.getLog()
            log.elem { "taskId" to taskId }

            val task = finishTaskService(FinishTaskCommand(taskId.toLong()))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        } catch (e: WebAppException.BadRequestException) {
            logger.error { "gRPC server error, invalid request." }
            responseObserver?.onError(
                    Status.INVALID_ARGUMENT.withDescription("invalid request.").asRuntimeException())
        }
    }
}