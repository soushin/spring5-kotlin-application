package app.web.routes

import app.ErrorItem
import app.grpc.server.gen.task.TaskOutbound
import app.json
import app.web.handler.CreateTaskInbound
import app.web.handler.TaskHandler
import app.web.handler.TaskModel
import app.KotlinModule.Companion.any
import app.WebAppException
import app.apidoc.ApiErrorExample
import app.apidoc.ApiParam
import app.apidoc.ApiSuccessExample
import app.apidoc.DefineBuilder
import app.mock
import app.util.DateUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.protobuf.Timestamp
import io.kotlintest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *
 * @author nsoushi
 */
@RunWith(SpringRunner::class)
class TaskRoutesTest {
    lateinit var client : WebTestClient
    lateinit var taskHandler: TaskHandler

    val mapper = ObjectMapper().registerModule(KotlinModule())

    @Before
    fun before() {

        taskHandler = mock(TaskHandler::class)

        val taskRoutes = TaskRoutes(taskHandler)

        client = WebTestClient.bindToRouterFunction(taskRoutes.taskRouter()).build()
    }

    val outbound = TaskOutbound.newBuilder()
            .setTaskId(1)
            .setTitle("task title")
            .setCreatedAt(getTimestamp(DateUtil.parse(DateUtil.Format.FULL_UTC)("2017-06-13T16:22:52Z")))
            .setUpdatedAt(getTimestamp(DateUtil.parse(DateUtil.Format.FULL_UTC)("2017-06-13T16:22:52Z")))
            .build()

    private fun getTimestamp(date: LocalDateTime): Timestamp.Builder {
        return Timestamp.newBuilder().setSeconds(java.sql.Timestamp.valueOf(date).toLocalDateTime()
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
    }

    val mockModel = TaskModel(outbound)

    /**
     * @api {GET} /api/task/:taskId タスクの取得
     * @apiName GetTask
     * @apiGroup Task
     * @apiVersion 1.0.0
     *
     * @apiUse GetTaskOk
     * @apiUse GetTaskNotFound
     *
     */
    @Test
    fun `GET Task`() {

        val define = DefineBuilder({
            version { "1.0.0" }
            name { "GetTaskOk" }
        })

        define.param { ApiParam("taskId", "タスクID", "23445", false, "7") }

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

                    define.success { actual }
                    define.successExample { ApiSuccessExample("Success", HttpStatus.OK, actual) }
                }

        define.genDoc()
    }
//
//    @Test
//    fun `GET Task NotFound`() {
//
//        val define = DefineBuilder({
//            version { "1.0.0" }
//            name { "GetTaskNotFound" }
//        })
//
//        // mock
//        `when`(taskHandler.fetchByTaskId(any())).thenThrow(WebAppException.NotFoundException("task notfound."))
//
//        client.get().uri("/api/task/1")
//                .accept(MediaType.APPLICATION_JSON_UTF8)
//                .exchange().expectStatus().isNotFound
//                .expectBody()
//                .consumeWith {
//                    val actual: ErrorItem = mapper.readValue(it.responseBody)
//
//                    define.error { actual }
//                    define.errorExample { ApiErrorExample("BadRequest", HttpStatus.BAD_REQUEST, actual) }
//                }
//
//        define.genDoc()
//    }

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
