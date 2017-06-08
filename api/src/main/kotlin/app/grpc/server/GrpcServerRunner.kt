package app.grpc.server

import app.config.AppProperties
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import mu.KotlinLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import java.util.*
import java.util.function.Consumer

/**
 *
 * @author nsoushi
 */
@Configuration
class GrpcServerRunner(private val appProperties: AppProperties,
                       private val echoServer: EchoServer,
                       private val taskServer: TaskServer) : CommandLineRunner, DisposableBean {

    private val logger = KotlinLogging.logger {}

    lateinit var server: Server

    override fun run(args: Array<String>) {

        val port = appProperties.grpc.server.port

        logger.info { "Starting gRPC Server ..." }
        val serverBuilder = NettyServerBuilder.forPort(port)
        serverBuilder.addService(echoServer)
        serverBuilder.addService(taskServer)
        server = serverBuilder.build().start()
        logger.info {"gRPC Server started, listening on port $port."}

        startDaemonAwaitThread()
    }

    private fun startDaemonAwaitThread() {
        val awaitThread = object : Thread() {
            override fun run() {
                try {
                    server.awaitTermination()
                } catch (e: InterruptedException) {
                    logger.error(e) { "gRPC server stopped." }
                }
            }
        }
        awaitThread.isDaemon = false
        awaitThread.start()
    }

    override fun destroy() {
        logger.info { "Shutting down gRPC server ..." }
        Optional.ofNullable(server).ifPresent(Consumer<Server> { it.shutdown() })
        logger.info {"gRPC server stopped."}
    }
}