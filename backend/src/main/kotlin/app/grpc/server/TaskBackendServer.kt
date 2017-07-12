package app.grpc.server

import app.WebAppException
import app.entity.Task
import app.grpc.server.gen.task.*
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
            val task = getTaskService(GetTaskCommand(request?.taskId!!.toLong()))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        }
    }

    override fun getTaskListService(request: TaskListInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            getTaskListService(GetTaskListCommand(request?.page!!))().forEach {
                val msg = getOutbound(it)
                responseObserver?.onNext(msg)
            }
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        }
    }

    override fun createTaskService(request: CreateTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val task = createTaskService(CreateTaskCommand(request?.title!!))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: Exception) {
            logger.error { "gRPC server error." }
            responseObserver?.onError(
                    Status.INTERNAL.withDescription("task error.").asRuntimeException())
        }
    }

    override fun updateTaskService(request: UpdateTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val task = updateTaskService(UpdateTaskCommand(request?.taskId!!.toLong(), request?.title!!))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        }
    }

    override fun deleteTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val task = deleteTaskService(DeleteTaskCommand(request?.taskId!!.toLong()))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        }
    }

    override fun finishTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val task = finishTaskService(FinishTaskCommand(request?.taskId!!.toLong()))
            val msg = getOutbound(task)
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        } catch (e: WebAppException.NotFoundException) {
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        }
    }
}