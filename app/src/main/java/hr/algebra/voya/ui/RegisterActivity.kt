package hr.algebra.voya.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import hr.algebra.voya.api.ApiClient
import hr.algebra.voya.api.TokenManager
import hr.algebra.voya.databinding.ActivityRegisterBinding
import hr.algebra.voya.model.ClientRegisterRequest
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegister.setOnClickListener {
            register()
        }

        binding.textLoginLink.setOnClickListener {
            finish()
        }
    }

    private fun register() {
        val email = binding.editTextEmail.text?.toString()?.trim().orEmpty()
        val password = binding.editTextPassword.text?.toString().orEmpty()
        val firstName = binding.editTextFirstName.text?.toString()?.trim().orEmpty()
        val lastName = binding.editTextLastName.text?.toString()?.trim().orEmpty()
        val phone = binding.editTextPhone.text?.toString()?.trim().orEmpty()

        if (email.isBlank() || password.isBlank() || firstName.isBlank() || lastName.isBlank() || phone.isBlank()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@RegisterActivity).clientRegister(
                    ClientRegisterRequest(
                        email = email,
                        password = password,
                        firstName = firstName,
                        lastName = lastName,
                        phone = phone
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null) {
                        Toast.makeText(this@RegisterActivity, "Registration failed: empty response", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    if (body.role != "CLIENT") {
                        TokenManager.clear(this@RegisterActivity)
                        Toast.makeText(this@RegisterActivity, "Only client accounts can use this app", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    TokenManager.saveToken(this@RegisterActivity, body.token)
                    TokenManager.saveUserId(this@RegisterActivity, body.userId)
                    TokenManager.saveUserProfile(
                        context = this@RegisterActivity,
                        email = body.email,
                        firstName = body.firstName,
                        lastName = body.lastName,
                        role = body.role,
                        phone = phone
                    )
                    Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, ReservationListActivity::class.java))
                    finishAffinity()
                    return@launch
                }

                when (response.code()) {
                    400 -> Toast.makeText(this@RegisterActivity, "Invalid registration data", Toast.LENGTH_SHORT).show()
                    409 -> Toast.makeText(this@RegisterActivity, "Email is already in use", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this@RegisterActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
