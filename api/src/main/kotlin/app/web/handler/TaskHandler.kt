package app.web.handler

import app.grpc.client.TaskBackendClient
import app.grpc.server.gen.task.TaskOutbound
import app.json
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.ServerResponse.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 *
 * @author nsoushi
 */
@Component
class TaskHandler(private val taskBackendClient: TaskBackendClient) {

    fun fetchByTaskId(req: ServerRequest) = ok().json().body(
            Mono.just(TaskModel(taskBackendClient.getTask(req.pathVariable("id").toLong()))))

    fun fetchAll(req: ServerRequest) = ok().json().body(Flux.fromIterable(taskBackendClient.getTaskList().map(::TaskModel)))

    fun create(req: ServerRequest) = ok().json().body(
            req.bodyToFlux(CreateTaskInbound::class.java).doOnNext {
                p -> TaskModel(taskBackendClient.createTask(p.title))
            })

    fun updateByTaskId(req: ServerRequest) = ok().json().body(
            req.bodyToFlux(CreateTaskInbound::class.java).doOnNext {
                p -> TaskModel(taskBackendClient.updateTask(req.pathVariable("id").toLong(), p.title))
            })

    fun deleteByTaskId(req: ServerRequest) = ok().json().body(
            Mono.just(TaskModel(taskBackendClient.deleteTask(req.pathVariable("id").toLong()))))

    fun finishByTaskId(req: ServerRequest) = ok().json().body(
            Mono.just(TaskModel(taskBackendClient.finishTask(req.pathVariable("id").toLong()))))
}

data class TaskModel(val id: Long, val title: String, val finishedAt: String?, val createdAt: String, val updatedAt: String) {
    constructor(entity: TaskOutbound) : this(
            id = entity.taskId.toLong(),
            title = entity.title,
            finishedAt = entity.finishedAt ?: null,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
    )
}

data class CreateTaskInbound(val title: String)