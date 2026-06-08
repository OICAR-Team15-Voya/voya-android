package hr.algebra.voya.model

data class ReservationRequest(
    val userId: Long,
    val vehicleCategoryId: Long,
    val time: String,
    val pickupLocation: String,
    val dropoffLocation: String,
    val passengerNumber: Int,
    val luggageNumber: Int,
    val welcomeSign: String,
    val additionalNotes: String?
)

data class ReservationDto(
    val id: Long,
    val userId: Long,
    val userFirstName: String,
    val userLastName: String,
    val userEmail: String,
    val vehicleCategoryId: Long,
    val vehicleCategoryName: String,
    val driverId: Long?,
    val driverFirstName: String?,
    val driverLastName: String?,
    val vehicleId: Long?,
    val vehicleName: String?,
    val vehicleRegistration: String?,
    val time: String,
    val pickupLocation: String,
    val dropoffLocation: String,
    val passengerNumber: Int,
    val luggageNumber: Int,
    val welcomeSign: String,
    val additionalNotes: String?,
    val status: String,
    val price: Double?,
    val isPaid: Boolean
)

data class VehicleCategoryDto(
    val id: Long,
    val name: String
)
