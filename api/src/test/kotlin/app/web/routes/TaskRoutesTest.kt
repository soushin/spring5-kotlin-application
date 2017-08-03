package app.web.routes

import app.grpc.server.gen.task.TaskOutbound
import app.json
import app.web.handler.CreateTaskInbound
import app.web.handler.TaskHandler
import app.web.handler.TaskModel
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
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 *
 * @author nsoushi
 */
@RunWith(SpringRunner::class)
class TaskRoutesTest {
    lateinit var client : WebTestClient
    lateinit var taskHandler: TaskHandler
    lateinit var exceptionFilter: ExceptionFilter

    val mapper = ObjectMapper().registerModule(KotlinModule())

    @Before
    fun before() {

        taskHandler = mock(TaskHandler::class)
        exceptionFilter = ExceptionFilter()

        val taskRoutes = TaskRoutes(taskHandler, exceptionFilter)

        client = WebTestClient.bindToRouterFunction(taskRoutes.taskRouter()).build()
    }

    val outbound = TaskOutbound.newBuilder()
            .setTaskId(1)
            .setTitle("task title")
            .setCreatedAt("2017-06-13T16:22:52Z")
            .setUpdatedAt("2017-06-13T16:22:52Z")
            .build()

    val mockModel = TaskModel(outbound)

    @Test
    fun `GET Task`() {

        // mock
        `when`(taskHandler.fetchByTaskId(any())).thenReturn(ok().json().body(Mono.just(mockModel)))

        client.get().uri("/api/task/1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange().expectStatus().isOk
                .expectBody()
                .consumeWith {
                    val actual: TaskModel = mapper.readValue(it.responseBody)
                    actual.id shouldBe 1L
                    actual.title shouldBe "task title"
                    actual.createdAt shouldBe "2017-06-13T16:22:52Z"
                    actual.updatedAt shouldBe "2017-06-13T16:22:52Z"
                }
    }

    @Test
    fun `GET Tasks`() {

        // mock
        `when`(taskHandler.fetchAll(any())).thenReturn(ok().json().body(Flux.fromIterable(listOf(mockModel))))

        client.get().uri("/api/tasks")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange().expectStatus().isOk
                .expectBody()
                .consumeWith {
                    val actual: List<TaskModel> = mapper.readValue(it.responseBody)
                    actual.size shouldBe 1
                    actual.get(0).id shouldBe 1L
                    actual.get(0).title shouldBe "task title"
                    actual.get(0).createdAt shouldBe "2017-06-13T16:22:52Z"
                    actual.get(0).updatedAt shouldBe "2017-06-13T16:22:52Z"
                }
    }

    @Test
    fun `CREATE Task`() {

        // mock
        `when`(taskHandler.create(any())).thenReturn(ok().json().body(Mono.just(mockModel)))

        client.post().uri("/api/task")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .syncBody(CreateTaskInbound(title = "title"))
                .exchange().expectStatus().isOk
                .expectBody()
                .consumeWith {
                    val actual: TaskModel = mapper.readValue(it.responseBody)
                    actual.id shouldBe 1L
                    actual.title shouldBe "task title"
                    actual.createdAt shouldBe "2017-06-13T16:22:52Z"
                    actual.updatedAt shouldBe "2017-06-13T16:22:52Z"
                }
    }

    @Test
    fun `UPDATE Task`() {

        // mock
        `when`(taskHandler.updateByTaskId(any())).thenReturn(ok().json().body(Mono.just(mockModel)))

        client.put().uri("/api/task/1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .syncBody(CreateTaskInbound(title = "title"))
                .exchange().expectStatus().isOk
                .expectBody()
                .consumeWith {
                    val actual: TaskModel = mapper.readValue(it.responseBody)
                    actual.id shouldBe 1L
                    actual.title shouldBe "task title"
                    actual.createdAt shouldBe "2017-06-13T16:22:52Z"
                    actual.updatedAt shouldBe "2017-06-13T16:22:52Z"
                }
    }

    @Test
    fun `DELETE Task`() {

        // mock
        `when`(taskHandler.deleteByTaskId(any())).thenReturn(ok().json().body(Mono.just(mockModel)))

        client.delete().uri("/api/task/1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange().expectStatus().isOk
                .expectBody()
                .consumeWith {
                    val actual: TaskModel = mapper.readValue(it.responseBody)
                    actual.id shouldBe 1L
                    actual.title shouldBe "task title"
                    actual.createdAt shouldBe "2017-06-13T16:22:52Z"
                    actual.updatedAt shouldBe "2017-06-13T16:22:52Z"
                }
    }
}
