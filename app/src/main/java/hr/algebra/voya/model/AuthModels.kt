package hr.algebra.voya.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class ClientRegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phone: String
)

data class AuthResponse(
    val token: String,
    val userId: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: String
)
