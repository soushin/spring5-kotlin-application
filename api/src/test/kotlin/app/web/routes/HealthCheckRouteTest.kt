package app.web.routes

import app.grpc.client.TaskBackendClient
import app.grpc.server.gen.task.TaskOutbound
import app.json
import app.web.handler.*
import io.kotlintest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 *
 * @author nsoushi
 */
@RunWith(SpringRunner::class)
class HealthCheckRouteTest {
    lateinit var client : WebTestClient
    lateinit var healthCheckHandler: HealthCheckHandler

    @Before
    fun before() {

        healthCheckHandler = mock(HealthCheckHandler::class)

        val healthCheckRoute = HealthCheckRoute(healthCheckHandler)

        client = WebTestClient.bindToRouterFunction(healthCheckRoute.healthCheckRouter()).build()
    }

    @Test
    fun `GET`() {

        // mock
        `when`(healthCheckHandler.healthy(km.any())).thenReturn(ok().json().body(Mono.just(Response(true))))

        val result = client.get().uri("/health_check")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange().expectStatus().isOk
                .expectBody(Response::class.java)
                .returnResult()

        val response = result.responseBody
        response.data shouldBe true
    }
}
