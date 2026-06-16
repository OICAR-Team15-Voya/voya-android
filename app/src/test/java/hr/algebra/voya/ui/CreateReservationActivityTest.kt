package hr.algebra.voya.ui

import hr.algebra.voya.util.ReservationDateTimeParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CreateReservationActivityTest {

    @Test
    fun parser_returnsIsoValueForValidDateAndTime() {
        val result = ReservationDateTimeParser.toIso("2026-06-11", "9:05")

        assertEquals("2026-06-11T09:05:00", result)
    }

    @Test
    fun parser_returnsNullForInvalidDate() {
        val result = ReservationDateTimeParser.toIso("2026-99-11", "09:05")

        assertNull(result)
    }

    @Test
    fun parser_returnsNullForInvalidTime() {
        val result = ReservationDateTimeParser.toIso("2026-06-11", "44:99")

        assertNull(result)
    }
}


