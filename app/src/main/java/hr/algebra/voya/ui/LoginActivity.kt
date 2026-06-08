package hr.algebra.voya.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import hr.algebra.voya.api.ApiClient
import hr.algebra.voya.api.TokenManager
import hr.algebra.voya.databinding.ActivityLoginBinding
import hr.algebra.voya.model.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener {
            login()
        }

        binding.textRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.editTextEmail.text?.toString()?.trim().orEmpty()
        val password = binding.editTextPassword.text?.toString().orEmpty()

        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.getApiService(this@LoginActivity).login(
                    LoginRequest(email = email, password = password)
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                    if (body == null) {
                        Toast.makeText(this@LoginActivity, "Login failed: empty response", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    TokenManager.saveToken(this@LoginActivity, body.token)
                    TokenManager.saveUserId(this@LoginActivity, body.userId)
                    TokenManager.saveUserProfile(
                        context = this@LoginActivity,
                        email = body.email,
                        firstName = body.firstName,
                        lastName = body.lastName,
                        role = body.role
                    )
                    startActivity(Intent(this@LoginActivity, ReservationListActivity::class.java))
                    finish()
                    return@launch
                }

                if (response.code() == 401) {
                    Toast.makeText(this@LoginActivity, "Pogrešan email ili lozinka", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (response.code() == 403) {
                    Toast.makeText(this@LoginActivity, "Samo klijentski računi mogu se prijaviti", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                Toast.makeText(this@LoginActivity, "Greška: ${response.code()}", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Greška mreže: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
