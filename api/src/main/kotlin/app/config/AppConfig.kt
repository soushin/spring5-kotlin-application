package app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

@Configuration
class AppConfig(val appProperties: AppProperties) {

    @Bean
    fun jedis(): JedisPool {
        val config = JedisPoolConfig()
        config.setMaxTotal(appProperties.redis.maxTotal)
        config.setMaxIdle(appProperties.redis.maxIdle)
        config.testOnBorrow = appProperties.redis.testOnBorrow
        val jedisPool = JedisPool(config, appProperties.redis.host, appProperties.redis.port, appProperties.redis.timeout)
        return jedisPool
    }

}
