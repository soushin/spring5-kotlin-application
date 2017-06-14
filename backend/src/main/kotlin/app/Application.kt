package app

import app.config.AppProperties
import app.dao.TaskDao
import app.entity.Task
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 *
 * @author nsoushi
 */
@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class Application {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder(Application::class).web(WebApplicationType.NONE).run(*args)
        }
    }
}
