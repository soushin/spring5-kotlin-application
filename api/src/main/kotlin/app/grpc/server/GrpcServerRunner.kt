package app.grpc.server

import app.config.AppProperties
import io.grpc.BindableService
import io.grpc.Server
import io.grpc.ServerInterceptors
import io.grpc.netty.NettyServerBuilder
import mu.KotlinLogging
import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.AbstractApplicationContext
import org.springframework.core.type.StandardMethodMetadata
import reactor.core.publisher.Flux
import reactor.core.publisher.toFlux
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 *
 * @author nsoushi
 */
@Configuration
class GRpcServerRunner(private val appProperties: AppProperties,
                       private val applicationContext: AbstractApplicationContext) : CommandLineRunner, DisposableBean {

    private val logger = KotlinLogging.logger {}

    lateinit var server: Server

    override fun run(args: Array<String>) {

        val port = appProperties.grpc.server.port
        val serverBuilder = serverBuilder()

        logger.info { "Starting gRPC Server ..." }

        getBeanNamesByTypeWithAnnotation(GRpcService::class).subscribe {
            name ->
            val server = applicationContext.beanFactory.getBean(name, BindableService::class) as BindableService
            val service = server.bindService()
            serverBuilder.addService(ServerInterceptors.intercept(service))
            logger.info { "$name service has been registered." }
        }

        server = serverBuilder.build().start()
        logger.info { "gRPC Server started, listening on port $port." }

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
        logger.info { "gRPC server stopped." }
    }

    private fun serverBuilder(): NettyServerBuilder {
        return NettyServerBuilder.forPort(appProperties.grpc.server.port)
    }

    private fun getBeanNamesByTypeWithAnnotation(annotationType: KClass<out Annotation>): Flux<String> {

        return applicationContext.getBeanNamesForType(BindableService::class.java).iterator().toFlux().filter {
            name ->
            val beanDefinition = applicationContext.beanFactory.getBeanDefinition(name)
            val beansWithAnnotation = applicationContext.getBeansWithAnnotation(annotationType.java)

            if (!beansWithAnnotation.isEmpty()) {
                beansWithAnnotation.containsKey(name)
            } else if (beanDefinition.source is StandardMethodMetadata) {
                val metadata = beanDefinition.source as StandardMethodMetadata
                metadata.isAnnotated(annotationType.simpleName)
            } else {
                false
            }
        }
    }
}