package app.grpc.client

import io.grpc.ManagedChannel

/**
 *
 * @author nsoushi
 */
object ShutdownLoan {
    fun <A : ManagedChannel, R> using(s: A, f: (A) -> R): R {
        try {
            return f(s)
        } finally {
            s.shutdown()
        }
    }
}
