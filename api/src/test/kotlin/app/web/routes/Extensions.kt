package app.web.routes

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.mockito.Mockito
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.reflect.KClass

/**
 *
 * @author nsoushi
 */

fun <T : Any> mock(type: KClass<T>) = Mockito.mock(type.java)

fun <T : Any> ObjectMapper.readValue(content: String, type: KClass<T>) = this.readValue(content, type.java)
