package app.config

import app.entity.Models
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import org.apache.commons.dbcp.BasicDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 *
 * @author nsoushi
 */
@Configuration
class AppConfig {

    @Bean
    fun realDataSource(): DataSource {
        val dataSource = BasicDataSource()
        dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
        dataSource.url = "jdbc:mysql://localhost:3306/todo?useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_general_ci&useSSL=false"
        dataSource.username = "root"
        dataSource.password = ""
        return dataSource
    }

    @Bean
    fun data(): KotlinEntityDataStore<Any> {
        val configuration = KotlinConfiguration(
                dataSource = realDataSource(),
                model = Models.KT,
                statementCacheSize = 0,
                useDefaultLogging = true)
        return KotlinEntityDataStore(configuration)
    }
}