package hr.algebra.voya.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import hr.algebra.voya.R
import hr.algebra.voya.api.ApiClient
import hr.algebra.voya.api.TokenManager
import hr.algebra.voya.model.ReservationDto
import kotlinx.coroutines.launch

class ReservationDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESERVATION_ID = "extra_reservation_id"
    }

    private lateinit var textReservationDetailTitle: TextView
    private lateinit var textReservationDetails: TextView
    private lateinit var progressReservationDetail: ProgressBar
    private lateinit var buttonRefreshDetail: Button
    private lateinit var buttonCancelReservation: Button
    private lateinit var buttonBackFromDetail: Button

    private var reservationId: Long = -1L
    private var currentReservationStatus: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation_detail)

        textReservationDetailTitle = findViewById(R.id.textReservationDetailTitle)
        textReservationDetails = findViewById(R.id.textReservationDetails)
        progressReservationDetail = findViewById(R.id.progressReservationDetail)
        buttonRefreshDetail = findViewById(R.id.buttonRefreshDetail)
        buttonCancelReservation = findViewById(R.id.buttonCancelReservation)
        buttonBackFromDetail = findViewById(R.id.buttonBackFromDetail)

        reservationId = intent.getLongExtra(EXTRA_RESERVATION_ID, -1L)
        if (reservationId <= 0L) {
            Toast.makeText(this, "Reservation ID is missing.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        buttonBackFromDetail.setOnClickListener {
            finish()
        }

        buttonRefreshDetail.setOnClickListener {
            loadReservationDetails()
        }

        buttonCancelReservation.setOnClickListener {
            cancelReservation()
        }

        loadReservationDetails()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_reservations -> {
                startActivity(Intent(this, ReservationListActivity::class.java))
                true
            }
            R.id.menu_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadReservationDetails() {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@ReservationDetailActivity)
                    .getReservationById(reservationId)

                if (response.isSuccessful) {
                    val reservation = response.body()
                    if (reservation == null) {
                        Toast.makeText(this@ReservationDetailActivity, "Reservation not found.", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    bindReservation(reservation)
                    return@launch
                }

                if (response.code() == 401) {
                    handleUnauthorized()
                    return@launch
                }

                Toast.makeText(this@ReservationDetailActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ReservationDetailActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun bindReservation(reservation: ReservationDto) {
        currentReservationStatus = reservation.status
        textReservationDetailTitle.text = "Reservation #${reservation.id}"
        textReservationDetails.text = buildString {
            appendLine("Status: ${reservation.status}")
            appendLine("Time: ${reservation.time}")
            appendLine("Vehicle category: ${reservation.vehicleCategoryName}")
            appendLine("Pickup: ${reservation.pickupLocation}")
            appendLine("Dropoff: ${reservation.dropoffLocation}")
            appendLine("Passengers: ${reservation.passengerNumber}")
            appendLine("Luggage: ${reservation.luggageNumber}")
            appendLine("Welcome sign: ${reservation.welcomeSign}")
            appendLine("Additional notes: ${reservation.additionalNotes ?: "-"}")
            appendLine("Paid: ${if (reservation.isPaid == true) "Yes" else "No"}")
            appendLine("Driver: ${reservation.driverFirstName ?: "-"} ${reservation.driverLastName ?: ""}".trim())
            appendLine("Vehicle: ${reservation.vehicleName ?: "-"}")
            appendLine("Registration: ${reservation.vehicleRegistration ?: "-"}")
        }

        // As defined in copilot-instructions.md, CANCELLED and COMPLETED cannot be updated/cancelled.
        val canCancel = reservation.status != "CANCELLED" && reservation.status != "COMPLETED"
        buttonCancelReservation.isEnabled = canCancel
        buttonCancelReservation.alpha = if (canCancel) 1f else 0.5f
    }

    private fun setLoading(isLoading: Boolean) {
        progressReservationDetail.isVisible = isLoading
        textReservationDetails.isVisible = !isLoading
        buttonRefreshDetail.isEnabled = !isLoading
        if (isLoading) {
            buttonCancelReservation.isEnabled = false
            buttonCancelReservation.alpha = 0.5f
        }
    }

    private fun cancelReservation() {
        if (currentReservationStatus == "CANCELLED" || currentReservationStatus == "COMPLETED") {
            Toast.makeText(this, "This reservation can no longer be cancelled.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@ReservationDetailActivity)
                    .cancelReservation(reservationId)

                if (response.isSuccessful) {
                    Toast.makeText(this@ReservationDetailActivity, "Reservation cancelled.", Toast.LENGTH_SHORT).show()
                    loadReservationDetails()
                    return@launch
                }

                if (response.code() == 401) {
                    handleUnauthorized()
                    return@launch
                }

                Toast.makeText(this@ReservationDetailActivity, "Cancel failed: ${response.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ReservationDetailActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun handleUnauthorized() {
        TokenManager.clear(this)
        Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}


