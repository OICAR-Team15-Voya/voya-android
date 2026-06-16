package hr.algebra.voya.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogoutMenuHelperTest {

    @Test
    fun resolveLogoutMenuId_usesExpectedResourceArguments() {
        var receivedName = ""
        var receivedType = ""
        var receivedPackage = ""

        val resolvedId = LogoutMenuHelper.resolveLogoutMenuId(
            resourceIdResolver = { name, type, packageName ->
                receivedName = name
                receivedType = type
                receivedPackage = packageName
                1234
            },
            packageName = "hr.algebra.voya"
        )

        assertEquals(1234, resolvedId)
        assertEquals("menu_logout", receivedName)
        assertEquals("id", receivedType)
        assertEquals("hr.algebra.voya", receivedPackage)
    }

    @Test
    fun isLogoutSelection_returnsTrueWhenIdsMatchAndResolvedIdIsValid() {
        val result = LogoutMenuHelper.isLogoutSelection(itemId = 88, logoutMenuId = 88)

        assertTrue(result)
    }

    @Test
    fun isLogoutSelection_returnsFalseWhenResolvedIdIsZero() {
        val result = LogoutMenuHelper.isLogoutSelection(itemId = 88, logoutMenuId = 0)

        assertFalse(result)
    }

    @Test
    fun isLogoutSelection_returnsFalseWhenIdsDoNotMatch() {
        val result = LogoutMenuHelper.isLogoutSelection(itemId = 88, logoutMenuId = 99)

        assertFalse(result)
    }
}

