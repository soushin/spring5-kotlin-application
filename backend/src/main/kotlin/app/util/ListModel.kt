package app.util

/**
 *
 * @author nsoushi
 */
data class ListModel<T> (private val list: List<T>) {
    operator fun invoke(): List<T> = this.list
}