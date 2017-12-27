package app.web.handler

import app.ErrorItem
import app.SystemException
import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.HandlerStrategies
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.result.view.ViewResolver
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

/**
 * @author soushin
 */
@Component
class ApiErrorHandler(private val objectMapper: ObjectMapper) : WebExceptionHandler {

    private val logger = KotlinLogging.logger {}

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        return handle(ex)
                .flatMap {
                    it.writeTo(exchange, HandlerStrategiesResponseContext(HandlerStrategies.withDefaults()))
                }
                .flatMap {
                    Mono.empty<Void>()
                }
    }

    private fun handle(t: Throwable): Mono<ServerResponse> {
        return when (t) {
            is SystemException -> {
                "api error".let {
                    logger.error(t) { t.message ?: it }
                    createResponse(t.status, t.message ?: it)
                }
            }
            is DecodingException -> {
                "invalid request".let {
                    logger.warn(t) { t.message ?: it }
                    createResponse(HttpStatus.BAD_REQUEST, it)
                }
            }
            else -> {
                logger.error(t) { "Unknown Exception: %s".format(t.message ?: "unknown error") }
                createResponse(HttpStatus.INTERNAL_SERVER_ERROR, t.message ?: "internal server error")
            }
        }
    }

    private fun createResponse(httpStatus: HttpStatus, message: String, code: String? = null): Mono<ServerResponse> {
        return Error(objectMapper.writeValueAsString(listOf(ErrorItem(message, code, null)))).let {
            ServerResponse.status(httpStatus).syncBody(it)
        }
    }
}

private class HandlerStrategiesResponseContext(val strategies: HandlerStrategies) : ServerResponse.Context {

    override fun messageWriters(): List<HttpMessageWriter<*>> {
        return this.strategies.messageWriters()
    }

    override fun viewResolvers(): List<ViewResolver> {
        return this.strategies.viewResolvers()
    }
}
