package app.config

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Slf4jReporter
import org.apache.commons.dbcp.BasicDataSource
import org.seasar.doma.jdbc.NoCacheSqlFileRepository
import org.seasar.doma.jdbc.SqlFileRepository
import org.seasar.doma.jdbc.dialect.Dialect
import org.seasar.doma.jdbc.dialect.MysqlDialect
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

/**
 *
 * @author nsoushi
 */
@Configuration
class AppConfig {

    fun realDataSource(): DataSource {
        val dataSource = BasicDataSource()
        dataSource.driverClassName = "com.mysql.cj.jdbc.Driver"
        dataSource.url = "jdbc:mysql://mysql:3306/todo?useUnicode=true&characterEncoding=UTF-8&connectionCollation=utf8mb4_general_ci&useSSL=false"
        dataSource.username = "root"
        dataSource.password = ""
        return dataSource
    }

    @Bean
    open fun dataSource(): DataSource = TransactionAwareDataSourceProxy(realDataSource())

    @Bean
    open fun dialect(): Dialect = MysqlDialect()

    @Bean
    open fun sqlFileRepository(): SqlFileRepository = NoCacheSqlFileRepository()

    @Bean
    fun metricRegistry(): MetricRegistry = MetricRegistry()

    @Bean
    fun reporter(): Slf4jReporter {
        val reporter = Slf4jReporter.forRegistry(metricRegistry())
                .outputTo(LoggerFactory.getLogger("GRPC_METRICS"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build()
        reporter.start(10, TimeUnit.MINUTES)
        return reporter
    }
}