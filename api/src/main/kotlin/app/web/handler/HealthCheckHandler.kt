package app.web.handler

import app.json
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import java.util.*

/**
 *
 * @author nsoushi
 */
@Component
class HealthCheckHandler() {
    fun healthy(req: ServerRequest) = ok().json().body(Mono.just(Response(true)))
}