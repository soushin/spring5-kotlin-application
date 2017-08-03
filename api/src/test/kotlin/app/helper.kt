package app

import org.mockito.Mockito
import kotlin.reflect.KClass

/**
 *
 * @author nsoushi
 */
class KotlinModule {
    companion object {
        fun <T> any(): T {
            return Mockito.any()
                    ?: null as T
        }

        fun <T : Any>  any(type: KClass<T>): T {
            return Mockito.any(type.java)
        }

        fun <T> eq(value: T): T {
            return if (value != null)
                Mockito.eq(value)
            else
                null
                        ?: null as T
        }
    }
}
