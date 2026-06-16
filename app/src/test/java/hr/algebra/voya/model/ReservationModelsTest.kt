package hr.algebra.voya.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReservationModelsTest {

    @Test
    fun reservationRequest_supportsNullableAdditionalNotes() {
        val request = ReservationRequest(
            userId = 1L,
            vehicleCategoryId = 2L,
            time = "2026-06-11T14:00:00",
            pickupLocation = "Airport",
            dropoffLocation = "Hotel",
            passengerNumber = 2,
            luggageNumber = 1,
            welcomeSign = "IVA",
            additionalNotes = null
        )

        assertNull(request.additionalNotes)
        assertEquals(2, request.passengerNumber)
    }

    @Test
    fun reservationDto_copyKeepsUnchangedFields() {
        val dto = ReservationDto(
            id = 10L,
            userId = 1L,
            userFirstName = "Iva",
            userLastName = "Ivic",
            userEmail = "client@example.com",
            vehicleCategoryId = 2L,
            vehicleCategoryName = "Sedan",
            driverId = null,
            driverFirstName = null,
            driverLastName = null,
            vehicleId = null,
            vehicleName = null,
            vehicleRegistration = null,
            time = "2026-06-11T14:00:00",
            pickupLocation = "Airport",
            dropoffLocation = "Hotel",
            passengerNumber = 2,
            luggageNumber = 1,
            welcomeSign = "IVA",
            additionalNotes = "No rush",
            status = "PENDING",
            price = 40.0,
            isPaid = false
        )

        val paid = dto.copy(status = "COMPLETED", isPaid = true)

        assertEquals("PENDING", dto.status)
        assertEquals("COMPLETED", paid.status)
        assertEquals("Sedan", paid.vehicleCategoryName)
        assertEquals(true, paid.isPaid)
    }

    @Test
    fun vehicleCategoryDto_storesIdAndName() {
        val category = VehicleCategoryDto(id = 3L, name = "Van")

        assertEquals(3L, category.id)
        assertEquals("Van", category.name)
    }
}

