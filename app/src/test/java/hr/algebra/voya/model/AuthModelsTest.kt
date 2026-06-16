package hr.algebra.voya.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class AuthModelsTest {

    @Test
    fun loginRequest_copyUpdatesOnlyChangedField() {
        val original = LoginRequest(email = "client@example.com", password = "secret123")

        val updated = original.copy(password = "newSecret")

        assertEquals("client@example.com", updated.email)
        assertEquals("newSecret", updated.password)
        assertNotEquals(original, updated)
    }

    @Test
    fun clientRegisterRequest_keepsValuesThroughDestructuring() {
        val request = ClientRegisterRequest(
            email = "client@example.com",
            password = "secret123",
            firstName = "Iva",
            lastName = "Ivic",
            phone = "+38591111222"
        )

        val (email, password, firstName, lastName, phone) = request

        assertEquals("client@example.com", email)
        assertEquals("secret123", password)
        assertEquals("Iva", firstName)
        assertEquals("Ivic", lastName)
        assertEquals("+38591111222", phone)
    }

    @Test
    fun authResponse_equalityIncludesAllFields() {
        val first = AuthResponse(
            token = "jwt",
            userId = 7L,
            email = "client@example.com",
            firstName = "Iva",
            lastName = "Ivic",
            role = "CLIENT"
        )
        val second = first.copy(role = "ADMIN")

        assertNotEquals(first, second)
        assertEquals("CLIENT", first.role)
        assertEquals("ADMIN", second.role)
    }
}

