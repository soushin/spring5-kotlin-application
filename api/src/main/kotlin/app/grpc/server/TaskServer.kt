package app.grpc.server

import app.grpc.GrpcException
import app.grpc.client.TaskBackendClient
import app.grpc.server.gen.task.TaskInbound
import app.grpc.server.gen.task.TaskOutbound
import app.grpc.server.gen.task.TaskServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver

/**
 *
 * @author nsoushi
 */
@GRpcService
class TaskServer(private val taskBackendClient: TaskBackendClient) : TaskServiceGrpc.TaskServiceImplBase() {

    override fun getTaskService(request: TaskInbound?, responseObserver: StreamObserver<TaskOutbound>?) {
        try {
            val outbound = taskBackendClient.getTask(request?.taskId!!.toLong())
            responseObserver?.onNext(outbound)
            responseObserver?.onCompleted()
        } catch (e: GrpcException) {
            val status = Status.fromThrowable(e)
            responseObserver?.onError(status.withDescription(e.message).asRuntimeException())
        }
    }
}