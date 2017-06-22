package app.web.routes

import com.fasterxml.jackson.core.type.TypeReference
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

inline fun <reified T : Any> ObjectMapper.readValue(content: String) = this.readValue(content, T::class.java)

inline fun <reified T : Any> ObjectMapper.readValueTypeReference(content: String) = this.readValue<T>(content, object : TypeReference<T>() {})
