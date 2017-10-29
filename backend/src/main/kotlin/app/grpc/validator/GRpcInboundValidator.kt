package app.grpc.validator

import app.WebAppException.BadRequestException
import app.grpc.server.gen.task.CreateTaskInbound
import app.grpc.server.gen.task.FindTaskInbound
import app.grpc.server.gen.task.GetTaskInbound
import app.grpc.server.gen.task.UpdateTaskInbound
import mu.KotlinLogging

/**
 *
 * @author nsoushi
 */
object GRpcInboundValidator {

    private val logger = KotlinLogging.logger {}
    private val DEFAULT_PAGE_LIMIT = 10

    fun validTaskInbound(request: GetTaskInbound?): String {

        request ?: throw BadRequestException("invalid request")

        val taskId = request.taskId.toString()

        if (taskId == "0")
            throw BadRequestException("invalid request")

        return taskId
    }

    fun validTaskListInbound(request: FindTaskInbound?): Array<String> {

        request ?: throw BadRequestException("invalid request")

        val page = when {
            request.hasPage() -> request.page.value
            else -> DEFAULT_PAGE_LIMIT
        }

        validPage(page)
        return arrayOf(page.toString())
    }

    fun validCreateTaskInbound(request: CreateTaskInbound?): Array<String> {
        request ?: throw BadRequestException("invalid request")

        val title = request.title.toString()
        validTitle(title)

        return arrayOf(title)
    }

    fun validUpdateTaskInbound(request: UpdateTaskInbound?): Array<String> {
        request ?: throw BadRequestException("invalid request")

        val title = request.title.toString()
        validTitle(title)
        val taskId = request.taskId.toString()

        return arrayOf(taskId, title)
    }

    private fun validPage(page: Int) {
        if (page <= 0)
            throw BadRequestException("server param error, 'page' parameter invalid")
    }

    private fun validTitle(title: String) {
        if (title.length > 50)
            throw BadRequestException("server param error, 'title' parameter invalid")
    }
}
