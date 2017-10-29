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
}
