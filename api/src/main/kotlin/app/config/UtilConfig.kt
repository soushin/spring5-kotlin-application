package app.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author soushin
 */
@Configuration
class UtilConfig {

    @Bean
    fun objectMapper() = ObjectMapper().registerModule(KotlinModule()).registerModule(JavaTimeModule())
}
