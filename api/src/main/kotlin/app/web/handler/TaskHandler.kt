package app.web.handler

import app.grpc.client.TaskBackendClient
import app.grpc.server.gen.task.TaskOutbound
import app.json
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.body
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.web.reactive.function.server.ServerResponse.*
import reactor.core.publisher.Mono

/**
 *
 * @author nsoushi
 */
@Component
class TaskHandler(private val taskBackendClient: TaskBackendClient) {
    fun findByTaskId(req: ServerRequest) =
        taskBackendClient.getTask(req.pathVariable("id").toLong()).fold({
            entity ->
            ok().json().body(Mono.just(TaskModel(entity)))
        }, {
            error ->
            status(NOT_FOUND).json().body(Mono.just(Error(error.message!!)))
        })
}

data class TaskModel(val id: Long, val title: String) {
    constructor(entity: TaskOutbound) : this(
            id = entity.taskId.toLong(),
            title = entity.title
    )
}