package hr.algebra.voya.api

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
class ApiClientTest {

    @Before
    fun setUp() {
        resetApiServiceSingleton()
    }

    @Test
    fun apiClient_containsExpectedConfigurationFields() {
        val declaredFieldNames = ApiClient::class.java.declaredFields.map { it.name }.toSet()

        assertTrue(declaredFieldNames.contains("BASE_URL"))
        assertTrue(declaredFieldNames.contains("apiService"))
    }

    @Test
    fun baseUrl_hasExpectedEmulatorEndpoint() {
        val field = ApiClient::class.java.getDeclaredField("BASE_URL")
        field.isAccessible = true

        val value = field.get(ApiClient) as String

        assertEquals("http://10.0.2.2:8080/", value)
    }

    @Test
    fun apiServiceSingletonField_isAccessibleAndNullable() {
        val field = ApiClient::class.java.getDeclaredField("apiService")
        field.isAccessible = true

        val initialValue = field.get(ApiClient)
        assertNotNull(field)
        assertTrue(initialValue == null || initialValue is ApiService)
    }

    @Test
    fun shouldAttachAuthHeader_returnsFalseForAuthEndpoints() {
        assertEquals(false, ApiClient.shouldAttachAuthHeader("/voya/api/auth/client-login"))
        assertEquals(false, ApiClient.shouldAttachAuthHeader("/voya/api/auth/clientRegister"))
    }

    @Test
    fun shouldAttachAuthHeader_returnsTrueForProtectedEndpoints() {
        assertEquals(true, ApiClient.shouldAttachAuthHeader("/voya/api/reservations/my-reservations"))
        assertEquals(true, ApiClient.shouldAttachAuthHeader("/voya/api/users/1/profile"))
    }

    private fun resetApiServiceSingleton() {
        val apiServiceField = ApiClient::class.java.getDeclaredField("apiService")
        apiServiceField.isAccessible = true
        apiServiceField.set(ApiClient, null)
    }
}


