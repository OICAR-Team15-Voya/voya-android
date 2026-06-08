# VOYA Android - Copilot Instructions

## Project Overview
This is the Android mobile client for the VOYA passenger transfer reservation system.
Built in Kotlin for Android (min SDK 26), using Views (not Jetpack Compose).
Keep the themes and UI simple and consistent throughout the app.

## Package
hr.algebra.voya

## Backend
- Spring Boot REST API running on http://10.0.2.2:8080 (Android emulator localhost)
- All endpoints prefixed with /voya/api/
- JWT authentication — token stored in SharedPreferences
- Every protected request needs header: Authorization: Bearer <token>

## Architecture
- Activities for each screen (no Fragments)
- Retrofit for HTTP calls
- Coroutines for async operations
- SharedPreferences for token and user data storage

## Dependencies in use
- Retrofit2 + Gson converter
- OkHttp3 + logging interceptor
- Kotlin Coroutines
- AndroidX AppCompat, ConstraintLayout

## Project Structure
hr.algebra.voya/
├── api/
│   ├── ApiClient.kt        ← Retrofit singleton + JWT interceptor
│   ├── ApiService.kt       ← all endpoint interface definitions
│   └── TokenManager.kt     ← SharedPreferences JWT storage
├── model/
│   ├── AuthModels.kt       ← login/register request and response DTOs
│   ├── ReservationModels.kt
│   └── UserModels.kt
└── ui/
├── LoginActivity.kt
├── RegisterActivity.kt
├── ReservationListActivity.kt
├── ReservationDetailActivity.kt
├── CreateReservationActivity.kt
├── UpdateReservationActivity.kt
└── ProfileActivity.kt

## API Endpoints

### Auth (public - no token needed)
POST /voya/api/auth/client-login
body: { email, password }
response: { token, userId, email, firstName, lastName, role }

POST /voya/api/auth/clientRegister
body: { email, password, firstName, lastName, phone }
response: { token, userId, email, firstName, lastName, role }

### Reservations (requires JWT)
GET  /voya/api/reservations/my-reservations         ← client's own reservations
GET  /voya/api/reservations/{id}                    ← single reservation
POST /voya/api/reservations                         ← create reservation
body: { userId, vehicleCategoryId, time, pickupLocation, dropoffLocation,
passengerNumber, luggageNumber, welcomeSign, additionalNotes }
PUT  /voya/api/reservations/my-reservations/{id}    ← update own reservation
body: same as create
PATCH /voya/api/reservations/cancel/{id}            ← cancel reservation

### Users (requires JWT)
PUT  /voya/api/users/{id}/profile                   ← update profile
body: { email, firstName, lastName, phone, role, status }
PUT  /voya/api/users/{id}/password                  ← change password
body: { oldPassword, newPassword }
DELETE /voya/api/users/forget-me/{id}               ← right to be forgotten

### Vehicle Categories (requires JWT)
GET /voya/api/vehicle-categories                    ← list all categories

## ReservationDto (what backend returns)
{
id, userId, userFirstName, userLastName, userEmail,
vehicleCategoryId, vehicleCategoryName,
driverId, driverFirstName, driverLastName,
vehicleId, vehicleName, vehicleRegistration,
time, pickupLocation, dropoffLocation,
passengerNumber, luggageNumber, welcomeSign, additionalNotes,
status, price, isPaid
}

## TokenManager usage
TokenManager.saveToken(context, token)
TokenManager.getToken(context)
TokenManager.getUserId(context)
TokenManager.clear(context)

## Important notes
- time field format: "2026-06-10T14:00:00" (ISO 8601)
- After login/register, save token AND userId to SharedPreferences
- On 401 response, clear token and redirect to LoginActivity
- CANCELLED and COMPLETED reservations cannot be updated
- Only CLIENT role can use this app (403 if DRIVER or ADMIN tries to login)
- Always handle errors gracefully and show user-friendly messages