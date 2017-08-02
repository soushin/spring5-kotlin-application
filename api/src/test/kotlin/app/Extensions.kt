package app

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import kotlin.reflect.KClass

/**
 *
 * @author nsoushi
 */

fun <T : Any> mock(type: KClass<T>) = Mockito.mock(type.java)
fun <T : Any> mockStatic(type: KClass<T>) = PowerMockito.mockStatic(type.java)

inline fun <reified T : Any> ObjectMapper.readValue(content: String) = this.readValue(content, T::class.java)

inline fun <reified T : Any> ObjectMapper.readValueTypeReference(content: String) = this.readValue<T>(content, object : TypeReference<T>() {})

fun ServerResponse.BodyBuilder.json() = contentType(MediaType.APPLICATION_JSON_UTF8)
