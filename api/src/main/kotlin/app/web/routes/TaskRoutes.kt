package app.web.routes

import app.ErrorItem
import app.SystemException
import app.json
import app.web.handler.TaskHandler
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.core.publisher.Mono

/**
 *
 * @author nsoushi
 */
@Configuration
class TaskRoutes(private val taskHandler: TaskHandler, private val exceptionFilter: ExceptionFilter) {

    @Bean
    fun taskRouter() = router {
        (accept(APPLICATION_JSON) and "/api").nest {
            "/task".nest {
                POST("/", taskHandler::create)
                GET("/{id}", taskHandler::fetchByTaskId)
                PUT("/{id}", taskHandler::updateByTaskId)
                DELETE("/{id}", taskHandler::deleteByTaskId)
                PUT("/{id}/finish", taskHandler::finishByTaskId)
            }
            "/tasks".nest {
                GET("/", taskHandler::fetchAll)
            }
        }
    }.filter(exceptionFilter())
}

@Component
class ExceptionFilter {

    private val logger = KotlinLogging.logger {}

    operator fun invoke(): (request: ServerRequest, next: HandlerFunction<ServerResponse>) -> Mono<ServerResponse> = { request, next ->
        try {
            next.handle(request)
        } catch (e: Exception) {
            when (e) {
                is SystemException -> status(e.status).json().body(Mono.just(ErrorItem(e.message ?: "web application error", e.status.value().toString(), null)))
                else -> {
                    logger.error(e) { "unknown exception: %s".format(e.message ?: "unknown error") }
                    status(HttpStatus.INTERNAL_SERVER_ERROR).json().body(Mono.just(ErrorItem(e.message ?: "internal server error", null, null)))
                }
            }
        }
    }
}