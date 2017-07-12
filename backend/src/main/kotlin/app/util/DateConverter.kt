package app.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 *
 * @author nsoushi
 */
object DateConverter {

    enum class Format(val format: String) {
        FULL("yyyy-MM-dd HH:mm:ss"),
        FULL_UTC("yyyy-MM-dd'T'HH:mm:ss'Z'"),
        FULL_ONLY_NUM("yyyyMMddHHmmss")
    }

    fun clearTime(date: LocalDateTime): LocalDateTime {
        return date.truncatedTo(ChronoUnit.DAYS)
    }

    infix fun LocalDateTime.to(f: DateConverter.Format) : String = DateTimeFormatter.ofPattern(f.format).format(this)

    infix fun String.`is`(f: DateConverter.Format) : LocalDateTime = LocalDateTime.parse(this, DateTimeFormatter.ofPattern(f.format))
}