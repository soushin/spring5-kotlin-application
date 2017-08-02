package app.web.routes

import app.json
import app.web.handler.*
import app.KotlinModule.Companion.any
import app.mock
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
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
import reactor.core.publisher.Mono

/**
 *
 * @author nsoushi
 */
@RunWith(SpringRunner::class)
class HealthCheckRouteTest {
    lateinit var client : WebTestClient
    lateinit var healthCheckHandler: HealthCheckHandler

    val mapper = ObjectMapper().registerModule(KotlinModule())

    @Before
    fun before() {

        healthCheckHandler = mock(HealthCheckHandler::class)

        val healthCheckRoute = HealthCheckRoute(healthCheckHandler)

        client = WebTestClient.bindToRouterFunction(healthCheckRoute.healthCheckRouter()).build()
    }

    @Test
    fun `GET`() {

        // mock
        `when`(healthCheckHandler.healthy(any())).thenReturn(ok().json().body(Mono.just(Response(true))))

        client.get().uri("/health_check")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .consumeWith {
                    val actual: Response<*> = mapper.readValue(it.responseBody)
                    actual.data shouldBe true
                }
    }
}
