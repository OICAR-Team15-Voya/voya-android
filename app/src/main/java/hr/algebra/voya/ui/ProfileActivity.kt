package hr.algebra.voya.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import hr.algebra.voya.R
import hr.algebra.voya.api.ApiClient
import hr.algebra.voya.api.TokenManager
import hr.algebra.voya.model.ChangePasswordRequest
import hr.algebra.voya.model.ProfileUpdateRequest
import hr.algebra.voya.util.LogoutMenuHelper
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var textUserIdProfile: TextView
    private lateinit var textProfileEmail: TextView
    private lateinit var editTextProfileFirstName: EditText
    private lateinit var editTextProfileLastName: EditText
    private lateinit var editTextProfilePhone: EditText
    private lateinit var editTextOldPassword: EditText
    private lateinit var editTextNewPassword: EditText
    private lateinit var buttonUpdateProfile: Button
    private lateinit var buttonChangePassword: Button
    private lateinit var buttonForgetMe: Button
    private lateinit var progressProfile: ProgressBar

    private var userId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        title = "Profile"

        textUserIdProfile = findViewById(R.id.textUserIdProfile)
        textProfileEmail = findViewById(R.id.textProfileEmail)
        editTextProfileFirstName = findViewById(R.id.editTextProfileFirstName)
        editTextProfileLastName = findViewById(R.id.editTextProfileLastName)
        editTextProfilePhone = findViewById(R.id.editTextProfilePhone)
        editTextOldPassword = findViewById(R.id.editTextOldPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile)
        buttonChangePassword = findViewById(R.id.buttonChangePassword)
        buttonForgetMe = findViewById(R.id.buttonForgetMe)
        progressProfile = findViewById(R.id.progressProfile)

        userId = TokenManager.getUserId(this) ?: -1L
        if (userId <= 0L) {
            handleUnauthorized()
            return
        }

        bindStoredProfile()

        buttonUpdateProfile.setOnClickListener {
            updateProfile()
        }

        buttonChangePassword.setOnClickListener {
            changePassword()
        }

        buttonForgetMe.setOnClickListener {
            confirmForgetMe()
        }
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
            R.id.menu_create_reservation -> {
                startActivity(Intent(this, CreateReservationActivity::class.java))
                true
            }
            R.id.menu_profile -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun bindStoredProfile() {
        textUserIdProfile.text = "User ID: $userId"
        textProfileEmail.text = "Email: ${TokenManager.getUserEmail(this).orEmpty()}"
        editTextProfileFirstName.setText(TokenManager.getUserFirstName(this).orEmpty())
        editTextProfileLastName.setText(TokenManager.getUserLastName(this).orEmpty())
        editTextProfilePhone.setText(TokenManager.getUserPhone(this).orEmpty())
    }

    private fun updateProfile() {
        val email = TokenManager.getUserEmail(this).orEmpty()
        val firstName = editTextProfileFirstName.text?.toString()?.trim().orEmpty()
        val lastName = editTextProfileLastName.text?.toString()?.trim().orEmpty()
        val phone = editTextProfilePhone.text?.toString()?.trim().orEmpty()
        val role = TokenManager.getUserRole(this) ?: "CLIENT"
        val status = TokenManager.getUserStatus(this) ?: "ACTIVE"

        if (email.isBlank() || firstName.isBlank() || lastName.isBlank() || phone.isBlank()) {
            Toast.makeText(this, "First name, last name and phone are required.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@ProfileActivity).updateProfile(
                    id = userId,
                    request = ProfileUpdateRequest(
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        phone = phone,
                        role = role,
                        status = status
                    )
                )

                if (response.isSuccessful) {
                    TokenManager.saveUserProfile(
                        context = this@ProfileActivity,
                        email = email,
                        firstName = firstName,
                        lastName = lastName,
                        role = role,
                        phone = phone,
                        status = status
                    )
                    Toast.makeText(this@ProfileActivity, "Profile updated.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (response.code() == 401) {
                    handleUnauthorized()
                    return@launch
                }

                Toast.makeText(this@ProfileActivity, "Update failed: ${response.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun changePassword() {
        val oldPassword = editTextOldPassword.text?.toString().orEmpty()
        val newPassword = editTextNewPassword.text?.toString().orEmpty()

        if (oldPassword.isBlank() || newPassword.isBlank()) {
            Toast.makeText(this, "Old and new password are required.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(this, "New password must be at least 6 characters.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@ProfileActivity).changePassword(
                    id = userId,
                    request = ChangePasswordRequest(oldPassword = oldPassword, newPassword = newPassword)
                )

                if (response.isSuccessful) {
                    editTextOldPassword.setText("")
                    editTextNewPassword.setText("")
                    Toast.makeText(this@ProfileActivity, "Password updated.", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (response.code() == 401) {
                    handleUnauthorized()
                    return@launch
                }

                Toast.makeText(this@ProfileActivity, "Password update failed: ${response.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun confirmForgetMe() {
        AlertDialog.Builder(this)
            .setTitle("Forget me")
            .setMessage("Are you sure? This will permanently remove your account.")
            .setPositiveButton("Yes") { _, _ ->
                forgetMe()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun forgetMe() {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@ProfileActivity).forgetMe(userId)

                if (response.isSuccessful) {
                    TokenManager.clear(this@ProfileActivity)
                    Toast.makeText(this@ProfileActivity, "Your account has been removed.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                    finishAffinity()
                    return@launch
                }

                if (response.code() == 401) {
                    handleUnauthorized()
                    return@launch
                }

                Toast.makeText(this@ProfileActivity, "Forget me failed: ${response.code()}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        progressProfile.isVisible = isLoading
        buttonUpdateProfile.isEnabled = !isLoading
        buttonChangePassword.isEnabled = !isLoading
        buttonForgetMe.isEnabled = !isLoading
    }

    private fun handleUnauthorized() {
        TokenManager.clear(this)
        Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }
}

