package hr.algebra.voya.model

data class ProfileUpdateRequest(
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val role: String,
    val status: String
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)
