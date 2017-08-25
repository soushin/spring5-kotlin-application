package app.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 *
 * @author nsoushi
 */
object DateUtil {

    enum class Format(val format: String) {
        FULL("yyyy-MM-dd HH:mm:ss"),
        FULL_UTC("yyyy-MM-dd'T'HH:mm:ss'Z'"),
        FULL_ONLY_NUM("yyyyMMddHHmmss")
    }

    fun clearTime(date: LocalDateTime): LocalDateTime {
        return date.truncatedTo(ChronoUnit.DAYS)
    }

    fun format(f: Format): (LocalDateTime) -> String {
        return { date -> DateTimeFormatter.ofPattern(f.format).format(date) }
    }

    fun parse(f: Format): (String) -> LocalDateTime {
        return { source -> LocalDateTime.parse(source, DateTimeFormatter.ofPattern(f.format)) }
    }

    infix fun LocalDateTime.to(f: DateUtil.Format) : String = DateTimeFormatter.ofPattern(f.format).format(this)

    infix fun String.`is`(f: DateUtil.Format) : LocalDateTime = LocalDateTime.parse(this, DateTimeFormatter.ofPattern(f.format))
}
