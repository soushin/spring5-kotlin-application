package app.web.routes

import app.web.handler.TaskHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.*
import org.springframework.web.reactive.function.server.*

/**
 *
 * @author nsoushi
 */
@Configuration
class TaskRoutes(private val taskHandler: TaskHandler) {

    @Bean
    fun taskRouter() = router {
        "/api".nest {
            accept(APPLICATION_JSON).nest {
                POST("/task", taskHandler::create)
                GET("/task/{id}", taskHandler::fetchByTaskId)
                PUT("/task/{id}", taskHandler::updateByTaskId)
                DELETE("/task/{id}", taskHandler::deleteByTaskId)
                PUT("/task/{id}/finish", taskHandler::finishByTaskId)
                GET("/tasks", taskHandler::fetchAll)
            }
            accept(TEXT_EVENT_STREAM).nest {
                GET("/task-count", taskHandler::fetchTaskCount)
            }
        }
    }
}
