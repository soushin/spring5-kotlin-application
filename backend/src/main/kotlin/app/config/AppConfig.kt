package app.config

import org.apache.commons.dbcp.BasicDataSource
import org.seasar.doma.jdbc.NoCacheSqlFileRepository
import org.seasar.doma.jdbc.SqlFileRepository
import org.seasar.doma.jdbc.dialect.Dialect
import org.seasar.doma.jdbc.dialect.MysqlDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
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
}