package app.web.routes

import app.web.handler.TaskHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

/**
 *
 * @author nsoushi
 */
@Configuration
class TaskRoutes(private val taskHandler: TaskHandler) {

    @Bean
    fun taskRouter() = router {
        (accept(APPLICATION_JSON) and "/api").nest {
            "/task".nest {
                GET("/{id}", taskHandler::findByTaskId)
            }
        }
    }
}