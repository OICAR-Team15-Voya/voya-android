package hr.algebra.voya.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import hr.algebra.voya.R
import hr.algebra.voya.api.ApiClient
import hr.algebra.voya.api.TokenManager
import hr.algebra.voya.model.ReservationRequest
import hr.algebra.voya.model.VehicleCategoryDto
import hr.algebra.voya.util.LogoutMenuHelper
import hr.algebra.voya.util.ReservationDateTimeParser
import kotlinx.coroutines.launch

class CreateReservationActivity : AppCompatActivity() {

    private lateinit var spinnerVehicleCategory: Spinner
    private lateinit var editTextDate: EditText
    private lateinit var editTextTime: EditText
    private lateinit var editTextPickupLocation: EditText
    private lateinit var editTextDropoffLocation: EditText
    private lateinit var editTextPassengerNumber: EditText
    private lateinit var editTextLuggageNumber: EditText
    private lateinit var editTextWelcomeSign: EditText
    private lateinit var editTextAdditionalNotes: EditText
    private lateinit var progressCreateReservation: ProgressBar
    private lateinit var buttonCreateReservation: Button
    private lateinit var buttonCancelReservation: Button

    private val vehicleCategories = mutableListOf<VehicleCategoryDto>()
    private lateinit var vehicleCategoryAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reservation)

        spinnerVehicleCategory = findViewById(R.id.spinnerVehicleCategory)
        editTextDate = findViewById(R.id.editTextDate)
        editTextTime = findViewById(R.id.editTextTime)
        editTextPickupLocation = findViewById(R.id.editTextPickupLocation)
        editTextDropoffLocation = findViewById(R.id.editTextDropoffLocation)
        editTextPassengerNumber = findViewById(R.id.editTextPassengerNumber)
        editTextLuggageNumber = findViewById(R.id.editTextLuggageNumber)
        editTextWelcomeSign = findViewById(R.id.editTextWelcomeSign)
        editTextAdditionalNotes = findViewById(R.id.editTextAdditionalNotes)
        progressCreateReservation = findViewById(R.id.progressCreateReservation)
        buttonCreateReservation = findViewById(R.id.buttonCreateReservation)
        buttonCancelReservation = findViewById(R.id.buttonCancelReservation)

        vehicleCategoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            mutableListOf<String>()
        )
        vehicleCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerVehicleCategory.adapter = vehicleCategoryAdapter

        buttonCancelReservation.setOnClickListener {
            finish()
        }

        buttonCreateReservation.setOnClickListener {
            submitReservation()
        }

        loadVehicleCategories()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val logoutMenuId = LogoutMenuHelper.resolveLogoutMenuId(resources::getIdentifier, packageName)
        if (LogoutMenuHelper.isLogoutSelection(item.itemId, logoutMenuId)) {
            TokenManager.clear(this)
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
            return true
        }

        return when (item.itemId) {
            R.id.menu_reservations -> {
                startActivity(Intent(this, ReservationListActivity::class.java))
                true
            }
            R.id.menu_create_reservation -> true
            R.id.menu_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadVehicleCategories() {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@CreateReservationActivity).getVehicleCategories()

                if (response.isSuccessful) {
                    vehicleCategories.clear()
                    vehicleCategories.addAll(response.body().orEmpty())

                    vehicleCategoryAdapter.clear()
                    vehicleCategoryAdapter.addAll(vehicleCategories.map { it.name })
                    vehicleCategoryAdapter.notifyDataSetChanged()

                    if (vehicleCategories.isEmpty()) {
                        Toast.makeText(this@CreateReservationActivity, "No vehicle categories available.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                if (response.code() == 401) {
                    handleUnauthorized()
                    return@launch
                }

                Toast.makeText(this@CreateReservationActivity, "Error loading categories: ${response.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@CreateReservationActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun submitReservation() {
        val userId = TokenManager.getUserId(this)
        if (userId == null) {
            handleUnauthorized()
            return
        }

        val selectedPosition = spinnerVehicleCategory.selectedItemPosition
        if (selectedPosition !in vehicleCategories.indices) {
            Toast.makeText(this, "Select a vehicle category.", Toast.LENGTH_SHORT).show()
            return
        }

        val dateInput = editTextDate.text?.toString()?.trim().orEmpty()
        val timeInput = editTextTime.text?.toString()?.trim().orEmpty()
        val pickupLocation = editTextPickupLocation.text?.toString()?.trim().orEmpty()
        val dropoffLocation = editTextDropoffLocation.text?.toString()?.trim().orEmpty()
        val passengerInput = editTextPassengerNumber.text?.toString()?.trim().orEmpty()
        val luggageInput = editTextLuggageNumber.text?.toString()?.trim().orEmpty()
        val welcomeSign = editTextWelcomeSign.text?.toString()?.trim().orEmpty()
        val additionalNotes = editTextAdditionalNotes.text?.toString()?.trim().orEmpty()

        if (
            dateInput.isBlank() ||
            timeInput.isBlank() ||
            pickupLocation.isBlank() ||
            dropoffLocation.isBlank() ||
            passengerInput.isBlank() ||
            luggageInput.isBlank() ||
            welcomeSign.isBlank() ||
            additionalNotes.isBlank()
        ) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show()
            return
        }

        val passengerNumber = passengerInput.toIntOrNull()
        if (passengerNumber == null || passengerNumber <= 0) {
            Toast.makeText(this, "Passenger number must be a positive integer.", Toast.LENGTH_SHORT).show()
            return
        }

        val luggageNumber = luggageInput.toIntOrNull()
        if (luggageNumber == null || luggageNumber < 0) {
            Toast.makeText(this, "Luggage number must be 0 or higher.", Toast.LENGTH_SHORT).show()
            return
        }

        val isoTime = parseToIsoDateTime(dateInput, timeInput)
        if (isoTime == null) {
            Toast.makeText(
                this,
                "Invalid date/time. Use date YYYY-MM-DD and time HH:mm. Required format is 2026-06-10T14:00:00.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val request = ReservationRequest(
                    userId = userId,
                    vehicleCategoryId = vehicleCategories[selectedPosition].id,
                    time = isoTime,
                    pickupLocation = pickupLocation,
                    dropoffLocation = dropoffLocation,
                    passengerNumber = passengerNumber,
                    luggageNumber = luggageNumber,
                    welcomeSign = welcomeSign,
                    additionalNotes = additionalNotes
                )

                val response = ApiClient.getApiService(this@CreateReservationActivity).createReservation(request)

                if (response.isSuccessful) {
                    Toast.makeText(this@CreateReservationActivity, "Reservation created successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                    return@launch
                }

                if (response.code() == 401) {
                    handleUnauthorized()
                    return@launch
                }

                Toast.makeText(this@CreateReservationActivity, "Create failed: ${response.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@CreateReservationActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun parseToIsoDateTime(dateInput: String, timeInput: String): String? {
        return ReservationDateTimeParser.toIso(dateInput = dateInput, timeInput = timeInput)
    }

    private fun setLoading(isLoading: Boolean) {
        progressCreateReservation.isVisible = isLoading
        buttonCreateReservation.isEnabled = !isLoading
        buttonCancelReservation.isEnabled = !isLoading
    }

    private fun handleUnauthorized() {
        TokenManager.clear(this)
        Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}


