package app.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * @author nsoushi
 */
@ConfigurationProperties("app")
class AppProperties {

    var grpc = Grpc()

    class Grpc {
        var backend = Backend()
        class Backend {
            var port = 50051
            var host = "backend"
        }
    }

    var redis = Redis()

    class Redis {
        var host: String = "redis"
        var port: Int = 6379
        var maxTotal: Int = 15
        var maxIdle: Int = 10
        var timeout: Int = 2000
        var testOnBorrow: Boolean = true
    }
}
