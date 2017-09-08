package app.grpc.server

import app.entity.Task
import app.grpc.handler.context.GRpcLogContextHandler
import app.grpc.server.gen.task.*
import app.grpc.validator.GRpcInboundValidator
import app.service.CreateTaskCommand
import app.service.CreateTaskService
import app.service.DeleteTaskCommand
import app.service.DeleteTaskService
import app.service.FindTaskCommand
import app.service.FindTaskService
import app.service.FinishTaskCommand
import app.service.FinishTaskService
import app.service.GetTaskCommand
import app.service.GetTaskService
import app.service.UpdateTaskCommand
import app.service.UpdateTaskService
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *
 * @author nsoushi
 */
@GRpcService
class TaskBackendServer(private val getTaskService: GetTaskService,
                        private val findTaskService: FindTaskService,
                        private val createTaskService: CreateTaskService,
                        private val updateTaskService: UpdateTaskService,
                        private val deleteTaskService: DeleteTaskService,
                        private val finishTaskService: FinishTaskService) : TaskServiceGrpc.TaskServiceImplBase() {

    private fun getOutbound(entity: Task): TaskOutbound {
        val builder = TaskOutbound.newBuilder()
                .setTaskId(entity.id!!)
                .setTitle(entity.title)
                .setCreatedAt(getTimestamp(entity.createdAt))
                .setUpdatedAt(getTimestamp(entity.updatedAt))

        if (entity.finishedAt != null)
            builder.setFinishedAt(getTimestamp(entity.finishedAt))

        return builder.build()
    }

    private fun getTimestamp(date: LocalDateTime): Timestamp.Builder {
        return Timestamp.newBuilder().setSeconds(date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    }

    override fun getTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val taskId = GRpcInboundValidator.validTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "taskId" to taskId }

        val task = getTaskService.getTask(GetTaskCommand(taskId.toLong()))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }

    override fun getTaskListService(request: TaskListInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val (page) = GRpcInboundValidator.validTaskListInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "page" to page }

        (findTaskService.findTask(FindTaskCommand(page.toInt())))().forEach {
            val msg = getOutbound(it)
            responseObserver?.onNext(msg)
        }
        responseObserver?.onCompleted()
    }

    override fun createTaskService(request: CreateTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val (title) = GRpcInboundValidator.validCreateTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "title" to title }

        val task = createTaskService.createTask(CreateTaskCommand(title))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }

    override fun updateTaskService(request: UpdateTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val (taskId, title) = GRpcInboundValidator.validUpdateTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "taskId" to taskId }
        log.elem { "title" to title }

        val task = updateTaskService.updateTask(UpdateTaskCommand(taskId.toLong(), title))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }

    override fun deleteTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val taskId = GRpcInboundValidator.validTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "taskId" to taskId }

        val task = deleteTaskService.deleteTask(DeleteTaskCommand(taskId.toLong()))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }

    override fun finishTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val taskId = GRpcInboundValidator.validTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "taskId" to taskId }

        val task = finishTaskService.finishTask(FinishTaskCommand(taskId.toLong()))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }
}
