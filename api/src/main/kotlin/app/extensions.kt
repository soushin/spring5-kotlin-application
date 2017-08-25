package app

import app.util.DateUtil
import ch.qos.logback.classic.pattern.DateConverter
import org.springframework.boot.SpringApplication
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.ServerResponse
import java.time.LocalDateTime
import kotlin.reflect.KClass

/**
 *
 * @author nsoushi
 */
fun run(type: KClass<*>, vararg args: String) = SpringApplication.run(type.java, *args)

fun ServerResponse.BodyBuilder.json() = contentType(MediaType.APPLICATION_JSON_UTF8)

fun LocalDateTime.convert(f: DateUtil.Format) = DateUtil.format(f)(this)
