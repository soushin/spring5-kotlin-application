package app.service

import app.grpc.client.TaskBackendClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPubSub
import reactor.core.publisher.FluxSink

@Service
class TaskService(private val jedisPool: JedisPool,
                  private val taskBackendClient: TaskBackendClient) {

    companion object {
        private val CHANNEL = "task"
    }

    private val flux = Flux.create<String> { emitter ->
        TaskSubscriber(emitter).let { sub ->
            jedisPool.resource.use {
                it.subscribe(sub, CHANNEL)
            }
        }
    }

    fun publishUpdateTask() {
        jedisPool.resource.use {
            it.publish(CHANNEL, "updateTask")
        }
    }

    fun subscribeTaskCount() =
            flux.map {
                taskBackendClient.getTaskCount().count
            }.run {
                share()
            }
}

class TaskSubscriber(private val fluxSink: FluxSink<String>) : JedisPubSub() {
    override fun onMessage(channel: String?, message: String?) {
        fluxSink.next(message)
    }
}
