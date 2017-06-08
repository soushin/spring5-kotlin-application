package app.grpc.server

import app.grpc.server.gen.task.TaskInbound
import app.grpc.server.gen.task.TaskOutbound
import app.grpc.server.gen.task.TaskServiceGrpc
import app.repository.TaskRepository
import io.grpc.Status
import io.grpc.stub.StreamObserver
import mu.KotlinLogging
import org.springframework.stereotype.Service

/**
 *
 * @author nsoushi
 */
@Service
class TaskBackendServer(private val taskRepository: TaskRepository) : TaskServiceGrpc.TaskServiceImplBase() {

    private val logger = KotlinLogging.logger {}

    override fun getTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        taskRepository.findOneById(request?.taskId!!.toLong()).fold({
            entity ->
            val msg = TaskOutbound.newBuilder().setTaskId(entity.id.toInt()).setTitle(entity.title).build()
            responseObserver?.onNext(msg)
            responseObserver?.onCompleted()
        }, {
            error ->
            logger.error { "gRPC server error, task not found." }
            responseObserver?.onError(
                    Status.NOT_FOUND.withDescription("task not found.").asRuntimeException())
        })
    }
}