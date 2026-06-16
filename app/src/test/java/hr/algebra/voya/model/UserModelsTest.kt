package hr.algebra.voya.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class UserModelsTest {

    @Test
    fun profileUpdateRequest_containsAllSubmittedFields() {
        val request = ProfileUpdateRequest(
            email = "client@example.com",
            firstName = "Iva",
            lastName = "Ivic",
            phone = "+38591111222",
            role = "CLIENT",
            status = "ACTIVE"
        )

        assertEquals("client@example.com", request.email)
        assertEquals("Iva", request.firstName)
        assertEquals("Ivic", request.lastName)
        assertEquals("+38591111222", request.phone)
        assertEquals("CLIENT", request.role)
        assertEquals("ACTIVE", request.status)
    }

    @Test
    fun changePasswordRequest_copyChangesOnlyNewPassword() {
        val original = ChangePasswordRequest(oldPassword = "oldpass", newPassword = "newpass1")

        val updated = original.copy(newPassword = "newpass2")

        assertEquals("oldpass", updated.oldPassword)
        assertEquals("newpass2", updated.newPassword)
        assertNotEquals(original, updated)
    }
}

