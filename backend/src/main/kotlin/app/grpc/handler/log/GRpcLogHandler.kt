package app.grpc.handler.log

import app.util.DateConverter.Format.FULL
import app.util.DateConverter.to
import java.time.LocalDateTime

/**
 *
 * @author nsoushi
 */
data class GRpcLog(val date: String,
                   val name: String,
                   val remoteAddr: String?,
                   val success: Boolean,
                   val body: Map<String, String>)

class GRpcLogBuilder() {

    constructor(init: GRpcLogBuilder.() -> Unit) : this() {
        init()
    }

    private var nameHolder: String = "defaultName"
    private var remoteAddrHolder: String? = null
    private var successHolder: Boolean = false
    private var elementHolder: Map<String, String> = mapOf()

    fun name(init: () -> String) {
        nameHolder = init()
    }

    fun remoteAddr(init: () -> String?) {
        remoteAddrHolder = init()
    }

    fun success(init: () -> Boolean) {
        successHolder = init()
    }

    fun elem(init: () -> Pair<String, String>) {
        elementHolder = elementHolder.plus(init())
    }

    fun build() = GRpcLog(
            date = LocalDateTime.now() to FULL,
            name = nameHolder,
            remoteAddr = remoteAddrHolder,
            success = successHolder,
            body = elementHolder)
}