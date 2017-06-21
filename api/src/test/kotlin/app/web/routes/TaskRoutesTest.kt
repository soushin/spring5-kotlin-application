package app.web.routes

import app.grpc.client.TaskBackendClient
import app.grpc.server.gen.task.TaskOutbound
import app.json
import app.web.handler.CreateTaskInbound
import app.web.handler.TaskHandler
import app.web.handler.TaskModel
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
    lateinit var taskBackendClient: TaskBackendClient

    @Before
    fun before() {

        taskBackendClient = mock(TaskBackendClient::class)
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
        `when`(taskHandler.fetchByTaskId(km.any())).thenReturn(ok().json().body(Mono.just(mockModel)))

        val result = client.get().uri("/api/task/1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange().expectStatus().isOk
                .expectBody(TaskModel::class.java)
                .returnResult()

        val response = result.responseBody
        response.id shouldBe 1L
        response.title shouldBe "task title"
        response.createdAt shouldBe "2017-06-13T16:22:52Z"
        response.updatedAt shouldBe "2017-06-13T16:22:52Z"
    }

    @Test
    fun `GET Tasks`() {

        // mock
        `when`(taskHandler.fetchAll(km.any())).thenReturn(ok().json().body(Flux.fromIterable(listOf(mockModel))))

        val result = client.get().uri("/api/tasks")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange().expectStatus().isOk
                .expectBodyList(TaskModel::class.java)
                .returnResult()

        val response = result.responseBody

        val task = response.get(0)
        task.id shouldBe 1L
        task.title shouldBe "task title"
        task.createdAt shouldBe "2017-06-13T16:22:52Z"
        task.updatedAt shouldBe "2017-06-13T16:22:52Z"
    }

    @Test
    fun `CREATE Task`() {

        // mock
        `when`(taskHandler.create(km.any())).thenReturn(ok().json().body(Mono.just(mockModel)))

        val result = client.post().uri("/api/task")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .syncBody(CreateTaskInbound(title = "title"))
                .exchange().expectStatus().isOk
                .expectBody(TaskModel::class.java)
                .returnResult()

        val response = result.responseBody
        response.id shouldBe 1L
        response.title shouldBe "task title"
        response.createdAt shouldBe "2017-06-13T16:22:52Z"
        response.updatedAt shouldBe "2017-06-13T16:22:52Z"
    }

    @Test
    fun `UPDATE Task`() {

        // mock
        `when`(taskHandler.updateByTaskId(km.any())).thenReturn(ok().json().body(Mono.just(mockModel)))

        val result = client.put().uri("/api/task/1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .syncBody(CreateTaskInbound(title = "title"))
                .exchange().expectStatus().isOk
                .expectBody(TaskModel::class.java)
                .returnResult()

        val response = result.responseBody
        response.id shouldBe 1L
        response.title shouldBe "task title"
        response.createdAt shouldBe "2017-06-13T16:22:52Z"
        response.updatedAt shouldBe "2017-06-13T16:22:52Z"
    }

    @Test
    fun `DELETE Task`() {

        // mock
        `when`(taskHandler.deleteByTaskId(km.any())).thenReturn(ok().json().body(Mono.just(mockModel)))

        val result = client.delete().uri("/api/task/1")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange().expectStatus().isOk
                .expectBody(TaskModel::class.java)
                .returnResult()

        val response = result.responseBody
        response.id shouldBe 1L
        response.title shouldBe "task title"
        response.createdAt shouldBe "2017-06-13T16:22:52Z"
        response.updatedAt shouldBe "2017-06-13T16:22:52Z"
    }
}
