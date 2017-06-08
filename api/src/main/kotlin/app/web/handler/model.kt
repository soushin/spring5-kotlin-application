package app.web.handler

/**
 *
 * @author nsoushi
 */
data class Response<T> (val data: T)

data class Error(val message: String)