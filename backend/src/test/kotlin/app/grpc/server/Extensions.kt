package app.grpc.server

import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import kotlin.reflect.KClass

fun <T : Any> mock(type: KClass<T>) = Mockito.mock(type.java)
fun <T : Any> mockStatic(type: KClass<T>) = PowerMockito.mockStatic(type.java)