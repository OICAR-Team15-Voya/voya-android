package hr.algebra.voya.api

import hr.algebra.voya.model.AuthResponse
import hr.algebra.voya.model.ChangePasswordRequest
import hr.algebra.voya.model.ClientRegisterRequest
import hr.algebra.voya.model.LoginRequest
import hr.algebra.voya.model.ProfileUpdateRequest
import hr.algebra.voya.model.ReservationDto
import hr.algebra.voya.model.ReservationRequest
import hr.algebra.voya.model.VehicleCategoryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    @POST("/voya/api/auth/client-login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("/voya/api/auth/clientRegister")
    suspend fun clientRegister(
        @Body request: ClientRegisterRequest
    ): Response<AuthResponse>

    @GET("/voya/api/reservations/my-reservations")
    suspend fun getMyReservations(): Response<List<ReservationDto>>

    @GET("/voya/api/reservations/{id}")
    suspend fun getReservationById(
        @Path("id") id: Long
    ): Response<ReservationDto>

    @POST("/voya/api/reservations")
    suspend fun createReservation(
        @Body request: ReservationRequest
    ): Response<ReservationDto>

    @PUT("/voya/api/reservations/my-reservations/{id}")
    suspend fun updateMyReservation(
        @Path("id") id: Long,
        @Body request: ReservationRequest
    ): Response<ReservationDto>

    @PATCH("/voya/api/reservations/cancel/{id}")
    suspend fun cancelReservation(
        @Path("id") id: Long
    ): Response<ReservationDto>

    @PUT("/voya/api/users/{id}/profile")
    suspend fun updateProfile(
        @Path("id") id: Long,
        @Body request: ProfileUpdateRequest
    ): Response<Void>

    @PUT("/voya/api/users/{id}/password")
    suspend fun changePassword(
        @Path("id") id: Long,
        @Body request: ChangePasswordRequest
    ): Response<Void>

    @DELETE("/voya/api/users/forget-me/{id}")
    suspend fun forgetMe(
        @Path("id") id: Long
    ): Response<Void>

    @GET("/voya/api/vehicle-categories")
    suspend fun getVehicleCategories(): Response<List<VehicleCategoryDto>>
}
