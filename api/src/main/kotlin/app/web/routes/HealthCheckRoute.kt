package app.web.routes

import app.web.handler.HealthCheckHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.router

/**
 *
 * @author nsoushi
 */
@Configuration
class HealthCheckRoute(private val healthCheckHandler: HealthCheckHandler) {

    @Bean
    fun healthCheckRouter() = router {
        (accept(MediaType.APPLICATION_JSON) and "/health_check").nest {
            GET("/", healthCheckHandler::healthy)
        }
    }
}