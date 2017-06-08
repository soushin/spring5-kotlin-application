package app.grpc.server

import app.grpc.client.TaskBackendClient
import app.grpc.server.gen.task.TaskInbound
import app.grpc.server.gen.task.TaskOutbound
import app.grpc.server.gen.task.TaskServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import mu.KotlinLogging
import org.springframework.stereotype.Service

/**
 *
 * @author nsoushi
 */
@Service
class TaskServer(private val taskBackendClient: TaskBackendClient) : TaskServiceGrpc.TaskServiceImplBase() {

    private val logger = KotlinLogging.logger {}

    override fun getTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {

        val res = taskBackendClient.getTask(request?.taskId!!.toLong())

        res.fold({
            success ->
            responseObserver?.onNext(success)
            responseObserver?.onCompleted()
        }, {
            error ->
            logger.error { "gRPC backend error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        })
    }
}