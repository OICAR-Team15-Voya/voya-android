package hr.algebra.voya.api

import org.junit.Assert.assertTrue
import org.junit.Test
class TokenManagerTest {

    @Test
    fun tokenManager_exposesExpectedPublicApi() {
        val methodNames = TokenManager::class.java.methods.map { it.name }.toSet()

        assertTrue(methodNames.contains("saveToken"))
        assertTrue(methodNames.contains("getToken"))
        assertTrue(methodNames.contains("saveUserId"))
        assertTrue(methodNames.contains("getUserId"))
        assertTrue(methodNames.contains("saveUserProfile"))
        assertTrue(methodNames.contains("clear"))
    }

    @Test
    fun tokenManager_containsExpectedPreferenceKeys() {
        val fieldNames = TokenManager::class.java.declaredFields.map { it.name }.toSet()

        assertTrue(fieldNames.contains("PREFS_NAME"))
        assertTrue(fieldNames.contains("KEY_TOKEN"))
        assertTrue(fieldNames.contains("KEY_USER_ID"))
        assertTrue(fieldNames.contains("KEY_USER_EMAIL"))
        assertTrue(fieldNames.contains("KEY_USER_FIRST_NAME"))
        assertTrue(fieldNames.contains("KEY_USER_LAST_NAME"))
        assertTrue(fieldNames.contains("KEY_USER_PHONE"))
        assertTrue(fieldNames.contains("KEY_USER_ROLE"))
        assertTrue(fieldNames.contains("KEY_USER_STATUS"))
    }
}


