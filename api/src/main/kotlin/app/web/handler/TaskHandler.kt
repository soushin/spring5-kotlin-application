package app.web.handler

import app.annotation.ApiDocProperty
import app.convert
import app.grpc.client.TaskBackendClient
import app.grpc.server.gen.task.TaskOutbound
import app.json
import app.service.TaskService
import app.util.DateUtil
import kotlinx.coroutines.experimental.runBlocking
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToServerSentEvents
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.ZoneId

/**
 *
 * @author nsoushi
 */
@Component
class TaskHandler(private val taskBackendClient: TaskBackendClient,
                  private val taskService: TaskService) {

    fun fetchByTaskId(req: ServerRequest) = ok().json().body(
            runBlocking {
                Mono.just(TaskModel(taskBackendClient.getTask(req.pathVariable("id").toLong())))
            })

    fun fetchAll(req: ServerRequest) = ok().json().body(Flux.fromIterable(
            runBlocking {
                taskBackendClient.getTaskList().map(::TaskModel)
            }))

    fun create(req: ServerRequest): Mono<ServerResponse> {
        return ok().json().body(
                req.bodyToMono(CreateTaskInbound::class.java).map { p ->
                    TaskModel(taskBackendClient.createTask(p.title)).also {
                        taskService.publishUpdateTask()
                    }
                })
    }

    fun updateByTaskId(req: ServerRequest) = ok().json().body(
            req.bodyToMono(CreateTaskInbound::class.java).map { p ->
                TaskModel(taskBackendClient.updateTask(req.pathVariable("id").toLong(), p.title))
            })

    fun deleteByTaskId(req: ServerRequest) = ok().json().body(
            Mono.just(
                    TaskModel(taskBackendClient.deleteTask(req.pathVariable("id").toLong()).also {
                        taskService.publishUpdateTask()
                    })))

    fun finishByTaskId(req: ServerRequest) = ok().json().body(
            Mono.just(TaskModel(taskBackendClient.finishTask(req.pathVariable("id").toLong()))))

    fun fetchTaskCount(req: ServerRequest): Mono<ServerResponse> {
        return ok().json().bodyToServerSentEvents(
                taskService.subscribeTaskCount()
                        .map {
                            TaskCount(it)
                        }
        )
    }
}

data class TaskModel(
        @ApiDocProperty(value = "タスクID", example = "23445")
        val id: Long,
        @ApiDocProperty(value = "タイトル", example = "牛乳を買う")
        val title: String,
        @ApiDocProperty(value = "完了日時", example = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        val finishedAt: String?,
        @ApiDocProperty(value = "作成日時", example = "yyyy-MM-dd'T'HH:mm:ss'Z'", nullable = true)
        val createdAt: String,
        @ApiDocProperty(value = "更新日時", example = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        val updatedAt: String) {
    constructor(entity: TaskOutbound) : this(
            id = entity.taskId.toLong(),
            title = entity.title,
            finishedAt = entity.hasFinishedAt().let {
                if (it)
                    Instant.ofEpochMilli(entity.finishedAt.seconds).atZone(ZoneId.systemDefault()).toLocalDateTime().convert(DateUtil.Format.FULL_UTC)
                else
                    null
            },
            createdAt = Instant.ofEpochMilli(entity.createdAt.seconds).atZone(ZoneId.systemDefault()).toLocalDateTime().convert(DateUtil.Format.FULL_UTC),
            updatedAt = Instant.ofEpochMilli(entity.updatedAt.seconds).atZone(ZoneId.systemDefault()).toLocalDateTime().convert(DateUtil.Format.FULL_UTC)
    )
}

data class CreateTaskInbound(val title: String)

data class TaskCount(val count: Int)
