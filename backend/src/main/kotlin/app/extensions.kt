package app

import org.springframework.boot.SpringApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import kotlin.reflect.KClass

/**
 *
 * @author nsoushi
 */
fun run(type: KClass<*>, vararg args: String) = SpringApplication.run(type.java, *args)

fun ServerResponse.BodyBuilder.json() = contentType(MediaType.APPLICATION_JSON_UTF8)

fun SpringApplicationBuilder(type: KClass<*>) = SpringApplicationBuilder(type.java)
