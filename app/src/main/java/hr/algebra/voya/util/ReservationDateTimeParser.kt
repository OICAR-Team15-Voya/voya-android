package hr.algebra.voya.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ReservationDateTimeParser {

    fun toIso(dateInput: String, timeInput: String): String? {
        return try {
            val date = LocalDate.parse(dateInput, DateTimeFormatter.ISO_LOCAL_DATE)
            val time = LocalTime.parse(timeInput, DateTimeFormatter.ofPattern("H:mm"))
            LocalDateTime.of(date, time).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        } catch (_: Exception) {
            null
        }
    }
}

