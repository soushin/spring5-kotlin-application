package app.grpc.validator

import app.WebAppException.BadRequestException
import app.grpc.server.gen.task.CreateTaskInbound
import app.grpc.server.gen.task.TaskInbound
import app.grpc.server.gen.task.TaskListInbound
import app.grpc.server.gen.task.UpdateTaskInbound
import mu.KotlinLogging

/**
 *
 * @author nsoushi
 */
object GRpcInboundValidator {

    private val logger = KotlinLogging.logger {}
    private val DEFAULT_PAGE_LIMIT = 10

    fun validTaskInbound(request: TaskInbound?): String {
        if (request == null)
            throw BadRequestException("invalid request")

        try {
            val taskId = request.taskId.toString()

            if(taskId == "0")
                throw BadRequestException("invalid request")

            return taskId
        } catch (e : Exception) {
            val msg = "grpc server error, invalid request"
            logger.error { msg }
            throw BadRequestException(msg)
        }
    }

    fun validTaskListInbound(request: TaskListInbound?): Array<String> {
        if (request == null)
            throw BadRequestException("invalid request")

        try {

            val page = when {
                request.hasPage() -> request.page.value
                else -> DEFAULT_PAGE_LIMIT
            }

            validPage(page)
            return arrayOf(page.toString())
        } catch (e : Exception) {
            val msg = "grpc server error, invalid request"
            logger.error { msg }
            throw BadRequestException(msg)
        }
    }

    fun validCreateTaskInbound(request: CreateTaskInbound?): Array<String> {
        if (request == null)
            throw BadRequestException("invalid request")

        try {
            val title = request.title.toString()
            validTitle(title)

            return arrayOf(title)
        } catch (e : Exception) {
            val msg = "grpc server error, invalid request"
            logger.error { msg }
            throw BadRequestException(msg)
        }
    }

    fun validUpdateTaskInbound(request: UpdateTaskInbound?): Array<String> {
        if (request == null)
            throw BadRequestException("invalid request")

        try {
            val title = request.title.toString()
            validTitle(title)
            val taskId = request.taskId.toString()

            return arrayOf(taskId, title)
        } catch (e : Exception) {
            val msg = "grpc server error, invalid request"
            logger.error { msg }
            throw BadRequestException(msg)
        }
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
