package app.grpc.server

import app.entity.Task
import app.grpc.handler.context.GRpcLogContextHandler
import app.grpc.server.gen.task.*
import app.grpc.validator.GRpcInboundValidator
import app.service.CreateTaskCommand
import app.service.DelegateTaskService
import app.service.DeleteTaskCommand
import app.service.FindTaskCommand
import app.service.FinishTaskCommand
import app.service.GetTaskCommand
import app.service.UpdateTaskCommand
import com.google.protobuf.Empty
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *
 * @author nsoushi
 */
@GRpcService
class TaskBackendServer(private val delegateTaskService: DelegateTaskService) : TaskServiceGrpc.TaskServiceImplBase() {

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

    override fun getTaskService(request: GetTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val taskId = GRpcInboundValidator.validTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "taskId" to taskId }

        val task = delegateTaskService.getTask(GetTaskCommand(taskId.toLong()))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }

    override fun findTaskService(request: FindTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val (page) = GRpcInboundValidator.validTaskListInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "page" to page }

        (delegateTaskService.findTask(FindTaskCommand(page.toInt())))().forEach {
            val msg = getOutbound(it)
            responseObserver?.onNext(msg)
        }
        responseObserver?.onCompleted()
    }

    override fun createTaskService(request: CreateTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val (title) = GRpcInboundValidator.validCreateTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "title" to title }

        val task = delegateTaskService.createTask(CreateTaskCommand(title))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }

    override fun updateTaskService(request: UpdateTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val (taskId, title) = GRpcInboundValidator.validUpdateTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "taskId" to taskId }
        log.elem { "title" to title }

        val task = delegateTaskService.updateTask(UpdateTaskCommand(taskId.toLong(), title))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }

    override fun deleteTaskService(request: GetTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val taskId = GRpcInboundValidator.validTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "taskId" to taskId }

        val task = delegateTaskService.deleteTask(DeleteTaskCommand(taskId.toLong()))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }

    override fun finishTaskService(request: GetTaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        val taskId = GRpcInboundValidator.validTaskInbound(request)

        val log = GRpcLogContextHandler.getLog()
        log.elem { "taskId" to taskId }

        val task = delegateTaskService.finishTask(FinishTaskCommand(taskId.toLong()))
        val msg = getOutbound(task)
        responseObserver?.onNext(msg)
        responseObserver?.onCompleted()
    }

    override fun getTaskCount(request: Empty?, responseObserver: StreamObserver<TaskCountOutbound>?) {
        responseObserver?.run {
            TaskCountOutbound.newBuilder()
                    .setCount(delegateTaskService.getCount())
                    .build().let {
                        onNext(it)
                        onCompleted()
                    }
        }
    }
}
